package com.smart.tcp.handler;

import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.em.CloseTypeEnum;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.server.service.ChannelService;
import com.smart.tcp.handler.biz.Event;
import com.smart.tcp.handler.biz.EventStrategy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
public class TcpServerHandler extends SimpleChannelInboundHandler<Object> {

    private ChannelService channelService;
    private EventStrategy eventStrategy;

    public TcpServerHandler(ChannelService channelService, EventStrategy eventStrategy) {
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
        // log.info("channelActive");
    }

    /**
     * 客户端连接断开前触发
     * 
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // log.info("channelInactive");
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

        if (msg instanceof Message) {

            Message message = (Message) msg;
            int cmd = message.getCmd();

            Event event = eventStrategy.build(CmdEnum.valueOf(cmd).getEventName());
            if (null == event) {
                throw new UnsupportedOperationException(String.format("cmd:%s Unsupported.", cmd));
            }

            event.onEvent(ctx, message);
        }

    }
}
