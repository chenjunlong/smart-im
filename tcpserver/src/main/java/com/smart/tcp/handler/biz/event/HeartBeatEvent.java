package com.smart.tcp.handler.biz.event;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.server.service.ChannelService;
import com.smart.tcp.handler.biz.AbstractEvent;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("heartBeatEvent")
public class HeartBeatEvent extends AbstractEvent {

    @Resource
    private ChannelService channelService;

    @Override
    public void execute(ChannelHandlerContext ctx, Message message) {

        // 消息体解析
        Message.Connect connect = Message.Connect.parseFromPb(message.getBody());


        // 心跳上报
        channelService.heartBeat(ctx, connect);


        // 心跳应答
        Message ack = Message.builder().build();
        ack.setCmd(CmdEnum.HEART_BEAT.getCmdId());
        ack.setSeq(System.nanoTime());


        ctx.writeAndFlush(ack);
    }
}
