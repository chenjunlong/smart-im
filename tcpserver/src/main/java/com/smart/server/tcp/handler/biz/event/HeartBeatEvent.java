package com.smart.server.tcp.handler.biz.event;

import com.smart.server.service.ChannelService;
import org.springframework.stereotype.Component;

import com.smart.server.tcp.codec.CodecObject;
import com.smart.server.tcp.handler.biz.AbstractEvent;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("heartBeatEvent")
public class HeartBeatEvent extends AbstractEvent {

    @Resource
    private ChannelService channelService;

    @Override
    public void execute(ChannelHandlerContext ctx, CodecObject codecObject) {
        channelService.heartBeat(ctx, codecObject);
    }
}
