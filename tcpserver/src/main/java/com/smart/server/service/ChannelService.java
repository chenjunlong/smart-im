package com.smart.server.service;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.smart.biz.common.kafka.Topic;
import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.UserInOutMessage;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.tcp.channel.ChannelRegistry;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Service
@Slf4j(topic = "connect")
public class ChannelService {


    @Resource(name = "smartImKafkaProducer")
    private KafkaProducer smartImKafkaProducer;



    /**
     * 注册TCP连接
     * 
     * @param ctx
     * @param connect
     */
    public void connect(ChannelHandlerContext ctx, Message.Connect connect) {

        long uid = connect.getUid();
        String roomId = connect.getRoomId();


        // 注册连接
        ChannelRegistry.add(roomId, uid, ctx.channel());


        // 发送进场消息
        UserInOutMessage userInOutMessage =
                UserInOutMessage.builder().uid(uid).roomId(roomId).cmd(CmdEnum.AUTH.getCmdId()).timestamp(System.currentTimeMillis()).build();
        this.sendUserInOutMessage(userInOutMessage);


        // 客户端连接日志
        String clientIp = ChannelRegistry.ChannelAttribute.getClientIp(ctx.channel());
        int port = ChannelRegistry.ChannelAttribute.getPort(ctx.channel());
        log.info(String.format("[ChannelService %s:%s connect]  roomId:%s, uid:%s join", clientIp, port, roomId, uid));
    }


    /**
     * 断开TCP连接
     * 
     * @param ctx
     * @param closeType
     */
    public void disconnect(ChannelHandlerContext ctx, int closeType) {

        Channel channel = ctx.channel();
        String roomId = ChannelRegistry.ChannelAttribute.getRoomId(channel);
        Long uid = ChannelRegistry.ChannelAttribute.getUid(channel);
        if (StringUtils.isBlank(roomId) && uid == 0L) {
            return;
        }


        // 移除连接
        ChannelRegistry.remove(roomId, uid, channel);


        // 发送出场消息
        UserInOutMessage userInOutMessage =
                UserInOutMessage.builder().uid(uid).roomId(roomId).cmd(CmdEnum.CLOSED.getCmdId()).timestamp(System.currentTimeMillis()).build();
        this.sendUserInOutMessage(userInOutMessage);


        // 客户端断开日志
        String clientIp = ChannelRegistry.ChannelAttribute.getClientIp(ctx.channel());
        int port = ChannelRegistry.ChannelAttribute.getPort(ctx.channel());
        log.info(String.format("[ChannelService %s:%s disconnect]  roomId:%s, uid:%s exit, closeType:%s", clientIp, port, roomId, uid, closeType));
    }


    /**
     * 客户端心跳事件
     * 
     * @param ctx
     * @param heartbeat
     */
    public void heartBeat(ChannelHandlerContext ctx, Message.Connect heartbeat) {

        long uid = heartbeat.getUid();
        String roomId = heartbeat.getRoomId();


        // 客户心跳开日志
        String clientIp = ChannelRegistry.ChannelAttribute.getClientIp(ctx.channel());
        int port = ChannelRegistry.ChannelAttribute.getPort(ctx.channel());
        log.info(String.format("[heart_beat %s:%s] roomId:%s, uid:%s", clientIp, port, roomId, uid));
    }


    /**
     * 扇出消息
     * 
     * @param message
     */
    public void send(Message message) {

        // 消息体解析
        Message.Body body = Message.Body.parseFromPb(message.getBody());


        // 查找房间用户
        Set<Long> uids = ChannelRegistry.getUids(body.getReceiveId(), body.getMsgType());
        if (CollectionUtils.isEmpty(uids)) {
            return;
        }

        StopWatch sw = new StopWatch();
        sw.start("下推数据");

        uids.stream().map(uid -> ChannelRegistry.getChannelByUid(uid)).filter(Objects::nonNull).forEach(channel -> channel.writeAndFlush(message));

        sw.stop();

        log.info(sw.prettyPrint());
    }


    /**
     * 用户进出场事件消息
     * 
     * @param userInOutMessage
     */
    private void sendUserInOutMessage(UserInOutMessage userInOutMessage) {
        String json = userInOutMessage.toJson();
        smartImKafkaProducer.send(new ProducerRecord(Topic.SMART_IM_MESSAGE_INOUT, json));
    }
}
