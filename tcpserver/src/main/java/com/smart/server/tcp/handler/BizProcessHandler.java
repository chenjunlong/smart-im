package com.smart.server.tcp.handler;

import com.smart.service.common.model.CmdEnum;
import com.smart.server.model.ConnectRequest;
import com.smart.server.tcp.channel.ChannelRegistry;
import com.smart.server.tcp.codec.CodecObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author chenjunlong
 */
@Slf4j
public class BizProcessHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 客户端连接断开时触发
     *
     * @param ctx
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        // 断连操作
        Channel channel = ctx.channel();
        String roomId = ChannelRegistry.ChannelAttribute.getRoomId(channel);
        Long uid = ChannelRegistry.ChannelAttribute.getUid(channel);
        ChannelRegistry.remove(roomId, uid, channel);

        // 断连日志记录
        String clientIp = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress) channel.remoteAddress()).getPort();
        log.info(String.format("[channelUnregistered %s:%s] roomId:%s, uid:%s exit.", clientIp, port, roomId, uid));
    }

    /**
     * 客户端连接建立成功时触发
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("channelInactive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == msg) {
            return;
        }

        if (msg instanceof CodecObject) {
            CodecObject codecObject = (CodecObject) msg;

            int cmd = codecObject.cmd;
            String body = new String(codecObject.body);


            Channel channel = ctx.channel();
            String clientIp = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
            int port = ((InetSocketAddress) channel.remoteAddress()).getPort();


            if (cmd == CmdEnum.HEART_BEAT.getCmdId()) { // 心跳消息
                log.info(String.format("[heart_beat %s:%s]", clientIp, port));

            } else if (cmd == CmdEnum.AUTH.getCmdId()) { // 加入房间鉴权消息
                ConnectRequest connectRequest = ConnectRequest.toObject(body, ConnectRequest.class);
                String roomId = connectRequest.getRoomId();
                Long uid = connectRequest.getUid();

                // TODO: 权限认证，通过后加入房间
                ChannelRegistry.add(roomId, uid, channel);
                log.info(String.format("[auth %s:%s] roomId:%s, uid:%s join.", clientIp, port, roomId, uid));

            } else if (cmd == CmdEnum.CLOSED.getCmdId()) { // 退出房间消息
                ConnectRequest connectRequest = ConnectRequest.toObject(body, ConnectRequest.class);
                String roomId = connectRequest.getRoomId();
                Long uid = connectRequest.getUid();

                ChannelRegistry.remove(roomId, uid, channel);
                log.info(String.format("[closed %s:%s] roomId:%s, uid:%s exit.", clientIp, port, roomId, uid));
            } else {
                throw new UnsupportedOperationException(String.format("cmd:%s Unsupported.", cmd));
            }

        }

    }
}
