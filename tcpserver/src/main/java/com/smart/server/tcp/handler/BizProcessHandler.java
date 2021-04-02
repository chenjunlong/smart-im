package com.smart.server.tcp.handler;

import java.net.InetSocketAddress;

import com.smart.server.model.ConnectRequest;
import com.smart.server.service.ChannelService;
import com.smart.server.tcp.channel.ChannelRegistry;
import com.smart.server.tcp.codec.CodecObject;
import com.smart.service.common.model.CmdEnum;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
public class BizProcessHandler extends SimpleChannelInboundHandler<Object> {

    private ChannelService channelService;

    public BizProcessHandler(ChannelService channelService) {
        this.channelService = channelService;
    }

    /**
     * 客户端连接断开时触发
     *
     * @param ctx
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        channelService.disconnet(ctx, "channelUnregistered");
    }

    /**
     * 客户端连接建立成功时触发
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channelActive");
    }

    /**
     * 客户端连接断开前触发
     * 
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("channelInactive");
    }

    /**
     * 读取客户端数据
     * 
     * @param ctx
     * @param msg
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
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

                channelService.disconnet(ctx, "CLOSED");
                log.info(String.format("[closed %s:%s] roomId:%s, uid:%s exit.", clientIp, port, roomId, uid));
            } else {
                throw new UnsupportedOperationException(String.format("cmd:%s Unsupported.", cmd));
            }

        }

    }
}
