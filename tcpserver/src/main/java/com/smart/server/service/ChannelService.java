package com.smart.server.service;

import com.smart.server.tcp.channel.ChannelRegistry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * @author chenjunlong
 */
@Service
@Slf4j(topic = "disconnet")
public class ChannelService {

    public void disconnet(ChannelHandlerContext ctx, String source) {
        Channel channel = ctx.channel();
        String roomId = ChannelRegistry.ChannelAttribute.getRoomId(channel);
        Long uid = ChannelRegistry.ChannelAttribute.getUid(channel);
        ChannelRegistry.remove(roomId, uid, channel);

        String clientIp = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress) channel.remoteAddress()).getPort();
        log.info(String.format("[ChannelService %s:%s] roomId:%s, uid:%s exit, source:%s", clientIp, port, roomId, uid, source));
    }
}
