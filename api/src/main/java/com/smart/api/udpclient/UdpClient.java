package com.smart.api.udpclient;


import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class UdpClient {

    private int port = 7998;
    private Channel channel;
    private EventLoopGroup eventLoopGroup;

    @PostConstruct
    public void start() {
        Bootstrap bootstrap = new Bootstrap();

        if (Epoll.isAvailable()) {
            eventLoopGroup = new EpollEventLoopGroup();
            bootstrap.channel(EpollDatagramChannel.class);
        } else {
            eventLoopGroup = new NioEventLoopGroup();
            bootstrap.channel(NioDatagramChannel.class);
        }

        bootstrap.group(eventLoopGroup).option(ChannelOption.SO_BROADCAST, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(8096));
        bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel ch) {
                ch.pipeline().addLast(new UdpClientHandler());
            }
        });
        try {
            channel = bootstrap.bind(port).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("[UdpClient] started success");
                }
            }).sync().channel();
        } catch (Exception e) {
            eventLoopGroup.shutdownGracefully();
            log.error("[UdpClient] started failure", e);
        }

    }


    public void send(byte[] data, String ip, int port) {
        ByteBuf buffer = channel.alloc().heapBuffer();
        buffer.writeBytes(data);
        DatagramPacket datagramPacket = new DatagramPacket(buffer, new InetSocketAddress(ip, port));
        channel.writeAndFlush(datagramPacket);
    }


    @PreDestroy
    public void stop() {
        log.info("[UdpClient] shutdown");
        eventLoopGroup.shutdownGracefully();
    }
}
