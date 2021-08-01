package com.smart.api.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.smart.api.udpclient.UdpClient;
import com.smart.biz.common.model.Comment;
import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.Pair;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.biz.common.model.em.MsgTypeEnum;
import com.smart.biz.dao.CommentDao;
import com.smart.biz.registry.RegistryProxy;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Service
public class MessageService {

    @Resource
    private CommentDao commentDao;
    @Resource(name = "udpRegistryProxy")
    private RegistryProxy udpRegistryProxy;
    @Resource
    private UdpClient udpClient;


    /**
     * 发送IM消息
     * 
     * @param senderId 发送方
     * @param receiveId 接收方
     * @param msgType 消息类型 0 p2p消息 1 房间消息 2 广播消息
     * @param cmd 消息命令
     * @param content 消息体
     * @return true 成功 false 失败
     */
    public boolean send(long senderId, String receiveId, int msgType, int cmd, String content) {

        long nowTimeMillis = System.currentTimeMillis();
        Message.Body body = Message.Body.builder().senderId(senderId).receiveId(receiveId).msgType(msgType).content(content).timestamp(nowTimeMillis).build();
        String message = Message.builder().cmd(cmd).seq(System.nanoTime()).body(body.toPbBytes()).build().toJson();


        List<String> addressList = udpRegistryProxy.getConnectAddress();
        addressList.stream().filter(StringUtils::isNotBlank).map(new Function<String, Pair<String, Integer>>() {
            @Nullable
            @Override
            public Pair apply(@Nullable String address) {
                String[] addressPort = address.split(":");
                return new Pair<>(addressPort[0], Integer.parseInt(addressPort[1]));
            }
        }).forEach(pair -> udpClient.send(message.getBytes(), pair.getK(), pair.getV()));


        return true;
    }

    /**
     * 发送评论
     * 
     * @param senderId 发送方
     * @param receiveId 接收方
     * @param content 消息体
     * @return true 成功 false 失败
     */
    public boolean comment(long senderId, String receiveId, String content) {
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
            boolean result = send(senderId, receiveId, MsgTypeEnum.ROOM.getType(), CmdEnum.COMMENT.getCmdId(), content);
            MDC.clear();
            return result;
        });

        AtomicBoolean result = new AtomicBoolean(true);
        createCommentFuture.whenComplete((v, t) -> result.set(result.get() & v));
        sendCommentFuture.whenComplete((v, t) -> result.set(result.get() & v));

        CompletableFuture.allOf(createCommentFuture, sendCommentFuture).join();
        return result.get();
    }

    /**
     * 获取最近10条评论
     * 
     * @param roomId 房间号
     * @return 最近10条评论按时间正序
     */
    public List<Comment> getLastCommentList(String roomId) {
        return commentDao.getLastCommentList(roomId);
    }
}
