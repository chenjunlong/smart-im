package com.smart.ws;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import com.smart.biz.common.model.em.CloseTypeEnum;
import com.smart.server.common.constant.Constant;
import com.smart.server.service.ChannelService;
import com.smart.server.tcp.channel.ChannelRegistry;
import com.smart.server.tcp.register.ServerRegistry;
import com.smart.ws.handler.WsServerChannelHandler;

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

/**
 * @author chenjunlong
 */
@Slf4j
public class WsServer {

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final int port;
    private final int bossThread;
    private final int workerThread;
    private final ChannelHandler channelHandler;
    private final ServerRegistry serverRegistry;
    private final ChannelService channelService;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap serverBootstrap;
    private ChannelFuture channelFuture;


    public WsServer(int port, int bossThread, int workerThread, WsServerChannelHandler channelHandler, ServerRegistry serverRegistry,
            ChannelService channelService) {
        this.port = port;
        this.bossThread = bossThread;
        this.workerThread = workerThread;
        this.channelHandler = channelHandler;
        this.serverRegistry = serverRegistry;
        this.channelService = channelService;
    }

    public void start() {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }

        log.info("[WsServer] start running...");

        this.buildServerBootstrap();

        try {
            // 启动tcp服务
            this.channelFuture = this.serverBootstrap.bind(port).sync();
            this.channelFuture.addListener((GenericFutureListener) future -> {
                if (future.isSuccess()) {
                    this.serverRegistry.register();
                    log.info("[WsServer] started {}", Constant.LOCAL_IP + ":" + port);
                }
            });

            Runtime.getRuntime().addShutdownHook(new ShutdownThread());

            // 监听tcp关闭事件
            this.channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("[WsServer] exception {}", e);
        }
    }

    private void buildServerBootstrap() {
        ThreadFactory bossGroupThreadFactory = new DefaultThreadFactory("WsBossGroup", true);
        ThreadFactory workerGroupThreadFactory = new DefaultThreadFactory("WsWorkerGroup", true);

        if (Epoll.isAvailable()) {
            this.bossGroup = new EpollEventLoopGroup(this.bossThread, bossGroupThreadFactory);
            this.workerGroup = new EpollEventLoopGroup(this.workerThread, workerGroupThreadFactory);
        } else {
            this.bossGroup = new NioEventLoopGroup(this.bossThread, bossGroupThreadFactory);
            this.workerGroup = new NioEventLoopGroup(this.workerThread, workerGroupThreadFactory);
        }

        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(this.bossGroup, this.workerGroup)
                .option(ChannelOption.SO_BACKLOG, 10240)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childHandler(this.channelHandler);

        if (Epoll.isAvailable()) {
            this.serverBootstrap.channel(EpollServerSocketChannel.class);
            log.info("[WsServer] use EpollServerSocketChannel");
        } else {
            this.serverBootstrap.channel(NioServerSocketChannel.class);
            log.info("[WsServer] use NioServerSocketChannel");
        }
    }

    class ShutdownThread extends Thread {
        @Override
        public void run() {

            // 取消注册
            try {
                serverRegistry.unregister();
            } catch (Exception e) {
                log.error(String.format("[WsServer] unregister failure %s", Constant.LOCAL_IP + ":" + port), e);
            }


            // 通知客户端断开
            try {
                ChannelRegistry.getAllChannel().parallelStream().forEach(channel -> channelService.disconnect(channel, CloseTypeEnum.STOP_SERVER.getType()));
            } catch (Exception e) {
                log.error(String.format("[WsServer] stop server notify client failure %s", Constant.LOCAL_IP + ":" + port), e);
            }


            // 关闭线程组
            try {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            } catch (Exception e) {
                log.error(String.format("[WsServer] close bossThreads and workerThreads failure %s", Constant.LOCAL_IP + ":" + port), e);
            }


            // 关闭channel
            try {
                channelFuture.channel().close().sync();
            } catch (InterruptedException e) {
                log.error(String.format("[WsServer] close channel failure %s", Constant.LOCAL_IP + ":" + port), e);
            }

            log.info("[WsServer] closed {}", Constant.LOCAL_IP + ":" + port);
        }
    }

}
