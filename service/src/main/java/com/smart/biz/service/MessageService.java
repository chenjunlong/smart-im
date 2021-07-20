package com.smart.biz.service;

import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.smart.biz.common.kafka.Topic;
import com.smart.biz.common.model.Message;

/**
 * @author chenjunlong
 */
@Service
public class MessageService {

    private Logger log = LoggerFactory.getLogger("send_kafka_message");

    @Resource(name = "smartImKafkaProducer")
    private Producer producer;

    public boolean send(long senderId, String receiveId, int boardCast, int cmd, String content) {
        Message.Body body = new Message.Body();
        body.senderId = senderId;
        body.receiveId = receiveId;
        body.boardCast = boardCast;
        body.content = content;
        String messageJson = new Message().build(cmd, body).toJson();

        Future<RecordMetadata> future = producer.send(new ProducerRecord(Topic.SMART_IM_MESSAGE, messageJson));
        try {
            future.get();
            log.info(messageJson);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean comment(long senderId, String receiveId, int boardCast, int cmdId, String content) {
        // 消息入库

        // 写入消息队列
        return this.send(senderId, receiveId, boardCast, cmdId, content);
    }
}
