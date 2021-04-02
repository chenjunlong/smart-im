package com.smart.server.tcp.handler;

import com.smart.server.service.ChannelService;
import com.smart.server.tcp.codec.SmartDecoder;
import com.smart.server.tcp.codec.SmartEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chenjunlong
 */
@Component
public class ServerChannelHandler extends ChannelInitializer<SocketChannel> {

    @Resource
    private ChannelService channelService;

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("timeout", new IdleStateHandler(300, 300, 0));
        ch.pipeline().addLast("connect_clear", new ServerIdleStateHandler(channelService));
        ch.pipeline().addLast("decoder", new SmartDecoder(Integer.MAX_VALUE, 0, 4, 0, 0, true));
        ch.pipeline().addLast("encoder", new SmartEncoder());
        ch.pipeline().addLast("handler", new BizProcessHandler(channelService));
    }

}
