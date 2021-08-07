package client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonParser;
import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.tcp.codec.SmartDecoder;
import com.smart.tcp.codec.SmartEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

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

        private String roomId = "room1001";
        private long uid = RandomUtils.nextLong();


        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) {
            // 启动线程重新连接
            System.err.println("重新开始连接...");
            doConnect(ctx);
        }

        // 读取数据
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

            int port = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
            Message message = (Message) msg;
            if (message.getCmd() == CmdEnum.COMMENT.getCmdId()) {
                Message.Body body = Message.Body.parseFromPb(message.getBody());
                System.out.println(String.format("接受到server响应数据(%s), cmd:%s, seq:%s, body:%s", port, message.getCmd(), message.getSeq(), body.toString()));
            } else {
                System.out.println(String.format("接受到server响应数据(%s), cmd:%s, seq:%s", port, message.getCmd(), message.getSeq()));
            }

        }

        // 发起连接，开启心跳
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

            Message.Connect connect = Message.Connect.builder().build();
            connect.setUid(uid);
            connect.setRoomId(roomId);


            Message message = Message.builder().build();
            message.setCmd(CmdEnum.AUTH.getCmdId());
            message.setSeq(System.nanoTime());
            message.setBody(connect.toPbBytes());


            ctx.writeAndFlush(message);
        }

        private void doHeartBeat(ChannelHandlerContext ctx) {

            Message.Connect connect = Message.Connect.builder().build();
            connect.setUid(uid);
            connect.setRoomId(roomId);


            Message message = Message.builder().build();
            message.setCmd(CmdEnum.HEART_BEAT.getCmdId());
            message.setSeq(System.nanoTime());
            message.setBody(connect.toPbBytes());


            ctx.writeAndFlush(message);
        }

        private void disConnect(ChannelHandlerContext ctx) {

            Message.Connect connect = Message.Connect.builder().build();
            connect.setUid(uid);
            connect.setRoomId(roomId);


            Message message = Message.builder().build();
            message.setCmd(CmdEnum.CLOSED.getCmdId());
            message.setSeq(System.nanoTime());
            message.setBody(connect.toPbBytes());


            ctx.writeAndFlush(message);
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
