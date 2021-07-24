package com.smart.server.tcp.handler.biz.event;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.smart.server.service.ChannelService;
import com.smart.server.tcp.codec.CodecObject;
import com.smart.server.tcp.handler.biz.AbstractEvent;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("authEvent")
public class AuthEvent extends AbstractEvent {

    @Resource
    private ChannelService channelService;

    @Override
    public void execute(ChannelHandlerContext ctx, CodecObject codecObject) {
        // TODO: 权限认证

        // 注册TCP连接
        channelService.connect(ctx, codecObject);
    }
}
