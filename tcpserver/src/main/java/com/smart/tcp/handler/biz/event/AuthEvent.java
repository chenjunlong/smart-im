package com.smart.tcp.handler.biz.event;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.server.service.ChannelService;
import com.smart.tcp.channel.ChannelRegistry;
import com.smart.tcp.handler.biz.AbstractEvent;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("authEvent")
public class AuthEvent extends AbstractEvent {

    private static final long MAX_CONNECTIONS = 20000L;

    @Resource
    private ChannelService channelService;

    @Override
    public void execute(ChannelHandlerContext ctx, Message message) {

        // TCP连接限流
        if (ChannelRegistry.getConnections() >= MAX_CONNECTIONS) {
            Message ack = Message.builder().build();
            ack.setCmd(CmdEnum.CONNECT_REFUSED_LIMIT.getCmdId());
            ack.setSeq(System.nanoTime());
            ctx.writeAndFlush(ack);
            return;
        }


        // 消息体解析
        Message.Connect connect = Message.Connect.parseFromPb(message.getBody());


        // 权限认证


        // 注册TCP连接
        channelService.connect(ctx, connect);


        // 连接成功应答
        Message ack = Message.builder().build();
        ack.setCmd(CmdEnum.AUTH.getCmdId());
        ack.setSeq(System.nanoTime());
        ctx.writeAndFlush(ack);

    }
}
