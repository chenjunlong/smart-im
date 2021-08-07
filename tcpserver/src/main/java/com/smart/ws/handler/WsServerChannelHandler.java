package com.smart.ws.handler;

import javax.annotation.Resource;

import com.smart.server.event.EventStrategy;
import org.springframework.stereotype.Component;

import com.smart.server.service.ChannelService;
import com.smart.tcp.handler.ServerIdleStateHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author chenjunlong
 */
@Component
public class WsServerChannelHandler extends ChannelInitializer<SocketChannel> {

    @Resource
    private ChannelService channelService;
    @Resource
    private EventStrategy eventStrategy;


    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast("timeout", new IdleStateHandler(300, 300, 0));
        ch.pipeline().addLast("connect_clear", new ServerIdleStateHandler(channelService));
        ch.pipeline().addLast("httpcodec", new HttpServerCodec());
        ch.pipeline().addLast("chunked", new ChunkedWriteHandler());
        ch.pipeline().addLast("http_object_aggregator", new HttpObjectAggregator(1024 * 64));
        ch.pipeline().addLast("websocket_protocol", new WebSocketServerProtocolHandler("/ws"));
        ch.pipeline().addLast("handler", new WsServerHandler(channelService, eventStrategy));
    }

}
