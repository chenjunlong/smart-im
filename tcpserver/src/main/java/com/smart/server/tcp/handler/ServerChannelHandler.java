package com.smart.server.tcp.handler;

import com.smart.server.tcp.codec.SmartDecoder;
import com.smart.server.tcp.codec.SmartEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import org.springframework.stereotype.Component;

/**
 * @author chenjunlong
 */
@Component
public class ServerChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("decoder", new SmartDecoder(Integer.MAX_VALUE, 0, 4, 0, 0, true));
        ch.pipeline().addLast("encoder", new SmartEncoder());
        ch.pipeline().addLast("timeout", new ServerIdleStateHandler(600, 120, 0));
        ch.pipeline().addLast("handler", new BizProcessHandler());
    }

}
