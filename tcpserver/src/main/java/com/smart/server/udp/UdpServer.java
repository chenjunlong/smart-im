package com.smart.server.udp;

import java.util.concurrent.atomic.AtomicBoolean;

import com.smart.server.common.constant.Constant;
import com.smart.server.service.ChannelService;
import com.smart.server.udp.handler.UdpServerHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 提供udp通信端口，用于和消息网关进程间通信
 * 
 * @author chenjunlong
 */
@Slf4j
public class UdpServer {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final String host;
    private final int port;

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private UdpRegistry udpRegistry;
    private ChannelService channelService;


    public UdpServer(int port, UdpRegistry udpRegistry, ChannelService channelService) {
        this.host = Constant.LOCAL_IP;
        this.port = port;
        this.udpRegistry = udpRegistry;
        this.channelService = channelService;
    }


    public synchronized void start() {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }

        log.info("[UdpServer] start running...");

        workerGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(8096))
                .handler(new ChannelInitializer<DatagramChannel>() {
            @Override
            protected void initChannel(DatagramChannel ch) {
                ch.pipeline().addLast(new UdpServerHandler(channelService));
            }
        });

        if (Epoll.isAvailable()) {
            bootstrap.channel(EpollDatagramChannel.class);
        } else {
            bootstrap.channel(NioDatagramChannel.class);
        }

        try {
            // 启动udp服务
            channelFuture = bootstrap.bind(this.host, this.port).sync();
            this.channelFuture.addListener((GenericFutureListener) future -> {
                if (future.isSuccess()) {
                    this.udpRegistry.register();
                    log.info("[UdpServer] started {}", this.host + ":" + this.port);
                }
            });

            Runtime.getRuntime().addShutdownHook(new ShutdownThread());
        } catch (Exception e) {
            log.error("[UdpServer] exception {}", e);
        }
    }

    class ShutdownThread extends Thread {
        @Override
        public void run() {
            // 取消注册
            try {
                udpRegistry.unregister();
            } catch (Exception e) {
                log.error(String.format("[UdpServer] unregister failure %s", host + ":" + port), e);
            }

            // 关闭线程组
            try {
                workerGroup.shutdownGracefully();
            } catch (Exception e) {
                log.error(String.format("[UdpServer] close workerThreads failure %s", host + ":" + port), e);
            }

            // 关闭channel
            try {
                channelFuture.channel().close().sync();
            } catch (InterruptedException e) {
                log.error(String.format("[UdpServer] close channel failure %s", host + ":" + port), e);
            }

            log.info("[UdpServer] closed {}", host + ":" + port);
        }
    }
}
