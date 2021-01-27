package com.smart.server.tcp.handler;

import com.smart.server.model.BehaviorEnum;
import com.smart.server.model.ConnectRequest;
import com.smart.server.tcp.channel.ChannelRegistry;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author chenjunlong
 */
@Slf4j
public class BizProcessHandler extends SimpleChannelInboundHandler<ConnectRequest> {

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        String roomId = ChannelRegistry.ChannelAttribute.getRoomId(channel);
        Long uid = ChannelRegistry.ChannelAttribute.getUid(channel);
        if (null == roomId || null == uid) {
            return;
        }
        ChannelRegistry.remove(roomId, uid, channel);
    }

    /**
     * {"roomId":roomId001, "uid":2002, "behavior": 1}
     * @param ctx
     * @param connectRequest
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectRequest connectRequest) {
        if (null == connectRequest) {
            return;
        }

        int behavior = connectRequest.getBehavior();
        String roomId = connectRequest.getRoomId();
        Long uid = connectRequest.getUid();

        Channel channel = ctx.channel();
        String clientIp = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress) channel.remoteAddress()).getPort();

        if (behavior == BehaviorEnum.JOIN.getBehavior()) {
            // TODO: 用户验证、房间验证
            ChannelRegistry.add(roomId, uid, channel);
            log.info(String.format("[%s:%s] roomId:%s, uid:%s join.", clientIp, port, roomId, uid));
        } else if (behavior == BehaviorEnum.EXIT.getBehavior()) {
            ChannelRegistry.remove(roomId, uid, channel);
            log.info(String.format("[%s:%s] roomId:%s, uid:%s exit.", clientIp, port, roomId, uid));
        } else {
            throw new UnsupportedOperationException(String.format("behavior:%s Unsupported.", behavior));
        }

    }
}
