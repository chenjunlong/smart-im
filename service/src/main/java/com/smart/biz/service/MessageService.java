package com.smart.biz.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.smart.biz.common.kafka.Topic;
import com.smart.biz.common.model.Comment;
import com.smart.biz.common.model.Message;
import com.smart.biz.dao.CommentDao;

/**
 * @author chenjunlong
 */
@Service
public class MessageService {

    private Logger log = LoggerFactory.getLogger("send_kafka_message");

    @Resource(name = "smartImKafkaProducer")
    private KafkaProducer smartImKafkaProducer;
    @Resource
    private CommentDao commentDao;


    public boolean send(long senderId, String receiveId, int msgType, int cmd, String content) {
        Message.Body body =
                Message.Body.builder().senderId(senderId).receiveId(receiveId).msgType(msgType).content(content).timestamp(System.currentTimeMillis()).build();
        String messageJson = Message.builder().cmd(cmd).body(body).build().toJson();
        Future<RecordMetadata> future = smartImKafkaProducer.send(new ProducerRecord(Topic.SMART_IM_MESSAGE, messageJson));
        try {
            future.get(1000, TimeUnit.MILLISECONDS);
            log.info(messageJson);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean comment(long senderId, String receiveId, int msgType, int cmdId, String content) {
        Map<String, String> map = MDC.getCopyOfContextMap();

        // 评论入库
        CompletableFuture<Boolean> createCommentFuture = CompletableFuture.supplyAsync(() -> {
            MDC.setContextMap(map);
            Comment comment = Comment.builder().senderId(senderId).receiveId(receiveId).content(content).build();
            boolean result = commentDao.createComment(comment);
            MDC.clear();
            return result;
        });

        // 发送评论消息
        CompletableFuture<Boolean> sendCommentFuture = CompletableFuture.supplyAsync(() -> {
            MDC.setContextMap(map);
            boolean result = send(senderId, receiveId, msgType, cmdId, content);
            MDC.clear();
            return result;
        });

        AtomicBoolean result = new AtomicBoolean(true);
        createCommentFuture.whenComplete((v, t) -> result.set(result.get() & v));
        sendCommentFuture.whenComplete((v, t) -> result.set(result.get() & v));

        CompletableFuture.allOf(createCommentFuture, sendCommentFuture).join();
        return result.get();
    }

    public List<Comment> getLastCommentList(String roomId) {
        return commentDao.getLastCommentList(roomId);
    }
}
