package com.smart.tcp.handler.biz.event;

import javax.annotation.Resource;

import com.smart.biz.common.model.em.CmdEnum;
import com.smart.tcp.channel.ChannelRegistry;
import org.springframework.stereotype.Component;

import com.smart.server.service.ChannelService;
import com.smart.tcp.codec.CodecObject;
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
    public void execute(ChannelHandlerContext ctx, CodecObject codecObject) {
        // TCP连接限流
        if (ChannelRegistry.getConnections() > MAX_CONNECTIONS) {
            CodecObject response = new CodecObject();
            response.cmd = CmdEnum.CONNECT_REFUSED_LIMIT.getCmdId();
            response.seq = System.nanoTime();
            ctx.writeAndFlush(response);
            return;
        }


        // TODO: 权限认证


        // 注册TCP连接
        channelService.connect(ctx, codecObject);

        CodecObject response = new CodecObject();
        response.cmd = CmdEnum.AUTH.getCmdId();
        response.seq = System.nanoTime();
        ctx.writeAndFlush(response);
    }
}
