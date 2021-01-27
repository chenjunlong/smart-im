package com.smart.server.tcp;

import com.smart.server.common.constant.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
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

/**
 * @author chenjunlong
 */
@Slf4j
public class NettyServer {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final int port;
    private final int bossThread;
    private final int workerThread;
    private final ChannelHandler channelHandler;
    private final ServerRegistry serverRegistry;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;


    public NettyServer(int port, int bossThread, int workerThread, ChannelHandler channelHandler, ServerRegistry serverRegistry) {
        this.port = port;
        this.bossThread = bossThread;
        this.workerThread = workerThread;
        this.channelHandler = channelHandler;
        this.serverRegistry = serverRegistry;
    }

    public synchronized void start() {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }
        log.info("[NettyServer] start running...");
        this.buildServerBootstrap();
        try {
            // 启动socket服务
            this.channelFuture = this.serverBootstrap.bind(port).sync();
            this.channelFuture.addListener((GenericFutureListener) future -> {
                if (future.isSuccess()) {
                    this.serverRegistry.register();
                    log.info("[NettyServer] started {}", Constant.LOCAL_IP + ":" + port);
                }
            });

            Runtime.getRuntime().addShutdownHook(new ShutdownThread());

            // 监听socket关闭事件
            this.channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("[NettyServer] exception {}", e);
        }
    }

    private void buildServerBootstrap() {
        ThreadFactory bossGroupThreadFactory = new DefaultThreadFactory("NettyBossGroup", true);
        ThreadFactory workerGroupThreadFactory = new DefaultThreadFactory("NettyWorkerGroup", true);

        if (Epoll.isAvailable()) {
            this.bossGroup = new EpollEventLoopGroup(this.bossThread, bossGroupThreadFactory);
            this.workerGroup = new EpollEventLoopGroup(this.workerThread, workerGroupThreadFactory);
        } else {
            this.bossGroup = new NioEventLoopGroup(this.bossThread, bossGroupThreadFactory);
            this.workerGroup = new NioEventLoopGroup(this.workerThread, workerGroupThreadFactory);
        }

        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(this.bossGroup, this.workerGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(this.channelHandler);

        if (Epoll.isAvailable()) {
            this.serverBootstrap.channel(EpollServerSocketChannel.class);
            log.info("[NettyServer] use EpollServerSocketChannel");
        } else {
            this.serverBootstrap.channel(NioServerSocketChannel.class);
            log.info("[NettyServer] use NioServerSocketChannel");
        }
    }

    class ShutdownThread extends Thread {
        @Override
        public void run() {
            // 取消注册
            try {
                serverRegistry.unregister();
            } catch (Exception e) {
                log.error(String.format("[NettyServer] unregister failure %s", Constant.LOCAL_IP + ":" + port), e);
            }

            // 关闭线程组
            try {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            } catch (Exception e) {
                log.error(String.format("[NettyServer] close bossThreads and workerThreads failure %s", Constant.LOCAL_IP + ":" + port), e);
            }

            // 关闭channel
            try {
                channelFuture.channel().close().sync();
            } catch (InterruptedException e) {
                log.error(String.format("[NettyServer] close channel failure %s", Constant.LOCAL_IP + ":" + port), e);
            }

            log.info("[NettyServer] closed {}", Constant.LOCAL_IP + ":" + port);
        }
    }
}
