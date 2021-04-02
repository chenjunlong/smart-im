package com.smart.server.tcp.client;

import com.google.gson.JsonParser;
import com.smart.server.model.ConnectRequest;
import com.smart.server.tcp.codec.CodecObject;
import com.smart.server.tcp.codec.SmartDecoder;
import com.smart.server.tcp.codec.SmartEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 * @author chenjunlong
 */
public class NettyClient {

    private String host;
    private int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

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
                System.out.println("连接服务器成功");
            } else {
                System.err.println("连接服务器失败");
                group.shutdownGracefully();
            }
        });
    }

    class ClientHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
            CodecObject codecObject = (CodecObject) msg;
            System.out.println("接受到server响应数据: " + codecObject.toString());
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);

            ConnectRequest connectRequest = new ConnectRequest();
            connectRequest.setRoomId("room1001");
            connectRequest.setUid(1001);

            CodecObject connReq = new CodecObject();
            connReq.cmd = 1;
            connReq.seq = System.currentTimeMillis();
            connReq.body = connectRequest.toJson().getBytes();
            ChannelFuture channelFuture = ctx.writeAndFlush(connReq);
            System.out.println(channelFuture.cause());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.close();
        }
    }

    public static void main(String[] args) throws Exception {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String response = restTemplate.getForObject("http://localhost:8000/v1/smart-im/dispatch/connect_address", String.class);
        String address = JsonParser.parseString(response).getAsJsonObject().getAsJsonArray("body").get(0).getAsString();

        String ip = address.split(":")[0];
        int port = Integer.parseInt(address.split(":")[1]);
        NettyClient nettyClient = new NettyClient(ip, port);
        nettyClient.start();
    }
}
