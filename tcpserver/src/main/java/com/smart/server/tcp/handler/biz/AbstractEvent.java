package com.smart.server.tcp.handler.biz;

import com.smart.server.tcp.codec.CodecObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * @author chenjunlong
 */
public abstract class AbstractEvent implements Event {

    private static final ThreadLocal<String> clientIp = new ThreadLocal<>();
    private static final ThreadLocal<Integer> clientPort = new ThreadLocal<>();

    @Override
    public void onEvent(ChannelHandlerContext ctx, CodecObject codecObject) {
        Channel channel = ctx.channel();
        String ip = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress) channel.remoteAddress()).getPort();

        clientIp.set(ip);
        clientPort.set(port);

        execute(ctx, codecObject);
    }

    public abstract void execute(ChannelHandlerContext ctx, CodecObject codecObject);

    public static String getClientIp() {
        return clientIp.get();
    }

    public static int getClientPort() {
        return clientPort.get();
    }
}
