package com.smart.server.tcp.handler;

import com.smart.server.tcp.channel.ChannelRegistry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author chenjunlong
 */
@Slf4j
public class ServerIdleStateHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            log.info("ServerIdleStateHandler state:{}, channel:{}", event.state(), ctx.channel().remoteAddress().toString());
            if (event.state() == IdleState.READER_IDLE) {
                Channel channel = ctx.channel();
                String roomId = ChannelRegistry.ChannelAttribute.getRoomId(channel);
                Long uid = ChannelRegistry.ChannelAttribute.getUid(channel);
                ChannelRegistry.remove(roomId, uid, channel);

                // 断连日志记录
                String clientIp = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
                int port = ((InetSocketAddress) channel.remoteAddress()).getPort();
                log.info(String.format("[ServerIdleStateHandler %s:%s] roomId:%s, uid:%s exit.", clientIp, port, roomId, uid));
            }
        }
    }
}
