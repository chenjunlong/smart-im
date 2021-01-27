package com.smart.server.tcp.handler;

import com.smart.server.tcp.codec.JsonDecoder;
import com.smart.server.tcp.codec.JsonEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;

/**
 * @author chenjunlong
 */
@Component
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
                //.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,1024 * 1024 * 1024, 0, 4, 0, 0, true))\
                .addLast(new LineBasedFrameDecoder(1024))
                .addLast(new JsonDecoder())
                .addLast(new JsonEncoder())
                .addLast(new BizProcessHandler());
    }

}
