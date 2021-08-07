package com.smart.ws.handler.event;

import javax.annotation.Resource;

import com.smart.server.event.base.AbstractEvent;
import com.smart.server.tcp.channel.ChannelRegistry;
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
@Component("authEvent")
public class AuthEvent extends AbstractEvent<Message.TextFrame> {

    private static final long MAX_CONNECTIONS = 20000L;

    @Resource
    private ChannelService channelService;

    @Override
    public void execute(ChannelHandlerContext ctx, Message.TextFrame textFrame) {

        // TCP连接限流
        if (ChannelRegistry.Connection.get() >= MAX_CONNECTIONS) {
            Message.TextFrame ack = Message.TextFrame.builder().build();
            ack.setCmd(CmdEnum.CONNECT_REFUSED_LIMIT.getCmdId());
            ctx.writeAndFlush(new TextWebSocketFrame(ack.toJson()));
            return;
        }


        // 消息体解析
        Message.Connect connect = Message.Connect.parseFromJson(Message.Connect.toJson(textFrame.getBody()), Message.Connect.class);


        // 权限认证


        // 注册TCP连接
        channelService.connect(ctx, connect);


        // 连接成功应答
        Message.TextFrame ack = Message.TextFrame.builder().build();
        ack.setCmd(CmdEnum.AUTH.getCmdId());
        ctx.writeAndFlush(new TextWebSocketFrame(ack.toJson()));

    }
}
