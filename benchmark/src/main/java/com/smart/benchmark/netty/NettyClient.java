package com.smart.benchmark.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.smart.benchmark.netty.model.*;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
public class NettyClient {

    private String host;
    private int port;
    private String roomId;
    private long uid;

    private EventLoopGroup group;

    public NettyClient(String host, int port, String roomId, long uid) {
        this.host = host;
        this.port = port;
        this.roomId = roomId;
        this.uid = uid;
    }

    public void start() throws Exception {
        group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast("decoder", new SmartDecoder(Integer.MAX_VALUE, 0, 4, 0, 0, true));
                ch.pipeline().addLast("encoder", new SmartEncoder());
                ch.pipeline().addLast("handler", new ClientHandler());
            }
        });


        ChannelFuture future = b.connect(host, port).sync();
        future.addListener((ChannelFutureListener) arg0 -> {
            if (future.isSuccess()) {
                // 连接服务器成功
            } else {
                // 连接服务器失败
                group.shutdownGracefully();
            }
        });
    }

    public void stop() {
        group.shutdownGracefully();
    }

    class ClientHandler extends SimpleChannelInboundHandler {

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) {

        }

        // 读取数据
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
            int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
            CodecObject codecObject = (CodecObject) msg;
            if (codecObject.cmd == 101) {
                Message.Body body = Message.Body.parseFromPb(codecObject.body, Message.Body.class);
                log.info(String.format("接受到server响应数据(%s), cmd:%s, body:%s", port, codecObject.cmd, body.toString()));
            }
        }

        // 鉴权，开启心跳
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            this.doConnect(ctx);
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> this.doHeartBeat(ctx), 10, 10, TimeUnit.SECONDS);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

        private void doConnect(ChannelHandlerContext ctx) {
            ConnectRequest connectRequest = new ConnectRequest();
            connectRequest.setRoomId(roomId);
            connectRequest.setUid(uid);

            CodecObject connReq = new CodecObject();
            connReq.cmd = 1;
            connReq.seq = System.currentTimeMillis();
            connReq.body = connectRequest.toPb();
            ctx.writeAndFlush(connReq);
        }

        private void doHeartBeat(ChannelHandlerContext ctx) {
            HeartBeatRequest heartBeatRequest = new HeartBeatRequest();
            heartBeatRequest.setRoomId(roomId);
            heartBeatRequest.setUid(uid);

            CodecObject heartBeatReq = new CodecObject();
            heartBeatReq.cmd = 0;
            heartBeatReq.seq = System.currentTimeMillis();
            heartBeatReq.body = heartBeatRequest.toPb();
            ctx.writeAndFlush(heartBeatReq);
        }
    }

}
