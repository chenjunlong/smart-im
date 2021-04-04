package com.smart.server.tcp.handler.biz.event;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.smart.server.model.DisconnectRequest;
import com.smart.server.service.ChannelService;
import com.smart.server.tcp.codec.CodecObject;
import com.smart.server.tcp.handler.biz.AbstractEvent;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("closedEvent")
public class ClosedEvent extends AbstractEvent {

    @Resource
    private ChannelService channelService;

    @Override
    public void execute(ChannelHandlerContext ctx, CodecObject codecObject) {
        DisconnectRequest disconnectRequest = DisconnectRequest.parseFromPb(codecObject.body, DisconnectRequest.class);
        String roomId = disconnectRequest.getRoomId();
        Long uid = disconnectRequest.getUid();

        channelService.disconnet(ctx, "CLOSED");
        log.info(String.format("[closed %s:%s] roomId:%s, uid:%s exit.", getClientIp(), getClientPort(), roomId, uid));
    }
}
