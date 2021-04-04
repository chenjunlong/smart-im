package com.smart.server.tcp.handler.biz.event;

import com.smart.server.model.ConnectRequest;
import com.smart.server.tcp.channel.ChannelRegistry;
import com.smart.server.tcp.codec.CodecObject;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.smart.server.tcp.handler.biz.AbstractEvent;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("authEvent")
public class AuthEvent extends AbstractEvent {

    @Override
    public void execute(ChannelHandlerContext ctx, CodecObject codecObject) {
        ConnectRequest connectRequest = ConnectRequest.parseFromPb(codecObject.body, ConnectRequest.class);
        String roomId = connectRequest.getRoomId();
        Long uid = connectRequest.getUid();

        // TODO: 权限认证通过后加入房间
        ChannelRegistry.add(roomId, uid, ctx.channel());
        log.info(String.format("[auth %s:%s] roomId:%s, uid:%s join.", getClientIp(), getClientPort(), roomId, uid));
    }
}
