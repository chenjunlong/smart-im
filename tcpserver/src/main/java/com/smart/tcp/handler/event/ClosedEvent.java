package com.smart.tcp.handler.event;

import javax.annotation.Resource;

import com.smart.biz.common.model.Message;
import org.springframework.stereotype.Component;

import com.smart.biz.common.model.em.CloseTypeEnum;
import com.smart.server.service.ChannelService;
import com.smart.server.event.base.AbstractEvent;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component("closedEvent")
public class ClosedEvent extends AbstractEvent<Message> {

    @Resource
    private ChannelService channelService;

    @Override
    public void execute(ChannelHandlerContext ctx, Message message) {
        channelService.disconnect(ctx.channel(), CloseTypeEnum.NORMAL.getType());
    }
}
