package com.smart.server.tcp.handler;

import com.smart.biz.common.model.em.CloseTypeEnum;
import com.smart.server.service.ChannelService;
import com.smart.server.tcp.codec.CodecObject;
import com.smart.server.tcp.handler.biz.Event;
import com.smart.server.tcp.handler.biz.EventStrategy;
import com.smart.biz.common.model.em.CmdEnum;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
public class BizProcessHandler extends SimpleChannelInboundHandler<Object> {

    private ChannelService channelService;
    private EventStrategy eventStrategy;

    public BizProcessHandler(ChannelService channelService, EventStrategy eventStrategy) {
        this.channelService = channelService;
        this.eventStrategy = eventStrategy;
    }

    /**
     * 客户端连接断开时触发
     *
     * @param ctx
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        channelService.disconnect(ctx, CloseTypeEnum.CLIENT.getType());
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

            Event event = eventStrategy.build(CmdEnum.valueOf(cmd).getEventName());
            if (null == event) {
                throw new UnsupportedOperationException(String.format("cmd:%s Unsupported.", cmd));
            }
            event.onEvent(ctx, codecObject);
        }
    }
}
