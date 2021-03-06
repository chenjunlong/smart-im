package com.smart.tcp.handler;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.smart.server.service.ChannelService;
import com.smart.tcp.codec.SmartDecoder;
import com.smart.tcp.codec.SmartEncoder;
import com.smart.server.event.EventStrategy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author chenjunlong
 */
@Component
public class TcpServerChannelHandler extends ChannelInitializer<SocketChannel> {

    @Resource
    private ChannelService channelService;
    @Resource
    private EventStrategy eventStrategy;

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("timeout", new IdleStateHandler(300, 300, 0));
        ch.pipeline().addLast("connect_clear", new ServerIdleStateHandler(channelService));
        ch.pipeline().addLast("decoder", new SmartDecoder(Integer.MAX_VALUE, 0, 4, 0, 0, true));
        ch.pipeline().addLast("encoder", new SmartEncoder());
        ch.pipeline().addLast("handler", new TcpServerHandler(channelService, eventStrategy));
    }

}
