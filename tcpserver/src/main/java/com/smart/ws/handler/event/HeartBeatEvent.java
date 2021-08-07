package com.smart.ws.handler.event;

import javax.annotation.Resource;

import com.smart.server.event.base.AbstractEvent;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Component;

import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.server.service.ChannelService;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("heartBeatEvent")
public class HeartBeatEvent extends AbstractEvent<Message.TextFrame> {

    @Resource
    private ChannelService channelService;

    @Override
    public void execute(ChannelHandlerContext ctx, Message.TextFrame textFrame) {

        // 消息体解析
        Message.Connect connect = Message.Connect.parseFromJson(Message.Connect.toJson(textFrame.getBody()), Message.Connect.class);


        // 心跳上报
        channelService.heartBeat(ctx, connect);


        // 心跳应答
        Message.TextFrame ack = Message.TextFrame.builder().build();
        ack.setCmd(CmdEnum.HEART_BEAT.getCmdId());
        ctx.writeAndFlush(new TextWebSocketFrame(ack.toJson()));
    }
}
