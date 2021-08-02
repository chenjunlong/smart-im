package com.smart.tcp;

import com.smart.biz.common.model.em.CloseTypeEnum;
import com.smart.server.common.constant.Constant;
import com.smart.server.service.ChannelService;
import com.smart.tcp.channel.ChannelRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * TcpServer：提供TCP端口服务
 *
 * @author chenjunlong
 */
@Slf4j
public class TcpServer {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final int port;
    private final int bossThread;
    private final int workerThread;
    private final ChannelHandler channelHandler;
    private final TcpRegistry tcpRegistry;
    private final ChannelService channelService;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;


    public TcpServer(int port, int bossThread, int workerThread, ChannelHandler channelHandler, TcpRegistry tcpRegistry, ChannelService channelService) {
        this.port = port;
        this.bossThread = bossThread;
        this.workerThread = workerThread;
        this.channelHandler = channelHandler;
        this.tcpRegistry = tcpRegistry;
        this.channelService = channelService;
    }

    public synchronized void start() {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }

        log.info("[TcpServer] start running...");

        this.buildServerBootstrap();

        try {
            // 启动tcp服务
            this.channelFuture = this.serverBootstrap.bind(port).sync();
            this.channelFuture.addListener((GenericFutureListener) future -> {
                if (future.isSuccess()) {
                    this.tcpRegistry.register();
                    log.info("[TcpServer] started {}", Constant.LOCAL_IP + ":" + port);
                }
            });

            Runtime.getRuntime().addShutdownHook(new ShutdownThread());

            // 监听tcp关闭事件
            this.channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("[TcpServer] exception {}", e);
        }
    }

    private void buildServerBootstrap() {
        ThreadFactory bossGroupThreadFactory = new DefaultThreadFactory("TcpBossGroup", true);
        ThreadFactory workerGroupThreadFactory = new DefaultThreadFactory("TcpWorkerGroup", true);

        if (Epoll.isAvailable()) {
            this.bossGroup = new EpollEventLoopGroup(this.bossThread, bossGroupThreadFactory);
            this.workerGroup = new EpollEventLoopGroup(this.workerThread, workerGroupThreadFactory);
        } else {
            this.bossGroup = new NioEventLoopGroup(this.bossThread, bossGroupThreadFactory);
            this.workerGroup = new NioEventLoopGroup(this.workerThread, workerGroupThreadFactory);
        }

        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(this.bossGroup, this.workerGroup).option(ChannelOption.SO_BACKLOG, 10240).option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.SO_REUSEADDR, true).childHandler(this.channelHandler);

        if (Epoll.isAvailable()) {
            this.serverBootstrap.channel(EpollServerSocketChannel.class);
            log.info("[TcpServer] use EpollServerSocketChannel");
        } else {
            this.serverBootstrap.channel(NioServerSocketChannel.class);
            log.info("[TcpServer] use NioServerSocketChannel");
        }
    }

    class ShutdownThread extends Thread {
        @Override
        public void run() {

            // 取消注册
            try {
                tcpRegistry.unregister();
            } catch (Exception e) {
                log.error(String.format("[TcpServer] unregister failure %s", Constant.LOCAL_IP + ":" + port), e);
            }


            // 通知客户端断开
            try {
                ChannelRegistry.getAllChannel().parallelStream().forEach(channel -> channelService.disconnect(channel, CloseTypeEnum.STOP_SERVER.getType()));
            } catch (Exception e) {
                log.error(String.format("[TcpServer] stop server notify client failure %s", Constant.LOCAL_IP + ":" + port), e);
            }


            // 关闭线程组
            try {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            } catch (Exception e) {
                log.error(String.format("[TcpServer] close bossThreads and workerThreads failure %s", Constant.LOCAL_IP + ":" + port), e);
            }

            // 关闭channel
            try {
                channelFuture.channel().close().sync();
            } catch (InterruptedException e) {
                log.error(String.format("[TcpServer] close channel failure %s", Constant.LOCAL_IP + ":" + port), e);
            }

            log.info("[TcpServer] closed {}", Constant.LOCAL_IP + ":" + port);
        }
    }
}
