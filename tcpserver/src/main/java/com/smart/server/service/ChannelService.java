package com.smart.server.service;

import com.smart.server.tcp.channel.ChannelRegistry;
import com.smart.server.tcp.codec.CodecObject;
import com.smart.service.common.model.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.Set;

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

    public void send(Message message) {
        Message.Body body = message.getBody();
        Set<Long> userSet = ChannelRegistry.getUids(body.receiveId, body.boardCast);
        for (long uid : userSet) {
            Channel channel = ChannelRegistry.getChannelByUid(uid);
            if (channel == null) {
                continue;
            }

            CodecObject codecObject = new CodecObject();
            codecObject.cmd = message.getCmd();
            codecObject.seq = System.nanoTime();
            codecObject.body = body.toPb();

            channel.writeAndFlush(codecObject);
        }
    }
}
