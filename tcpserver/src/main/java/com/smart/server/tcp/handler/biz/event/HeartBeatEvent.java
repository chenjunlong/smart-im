package com.smart.server.tcp.handler.biz.event;

import org.springframework.stereotype.Component;

import com.smart.server.tcp.codec.CodecObject;
import com.smart.server.tcp.handler.biz.AbstractEvent;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("heartBeatEvent")
public class HeartBeatEvent extends AbstractEvent {

    @Override
    public void execute(ChannelHandlerContext ctx, CodecObject codecObject) {
        log.info(String.format("[heart_beat %s:%s]", getClientIp(), getClientPort()));
    }
}
