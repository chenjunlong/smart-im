package com.smart.ws.handler;

import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.em.CloseTypeEnum;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.server.event.EventStrategy;
import com.smart.server.event.base.Event;
import com.smart.server.service.ChannelService;
import com.smart.server.tcp.channel.ChannelRegistry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
public class WsServerHandler extends SimpleChannelInboundHandler<Object> {

    private ChannelService channelService;
    private EventStrategy eventStrategy;

    public WsServerHandler(ChannelService channelService, EventStrategy eventStrategy) {
        this.channelService = channelService;
        this.eventStrategy = eventStrategy;
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ChannelRegistry.Connection.increment();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        ChannelRegistry.Connection.decrement();
        channelService.disconnect(ctx.channel(), CloseTypeEnum.NETWORK.getType());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrameMsg = (TextWebSocketFrame) msg;
            Message.TextFrame message = Message.parseFromJson(textFrameMsg.text(), Message.TextFrame.class);

            int cmd = message.getCmd();

            Event event = eventStrategy.build(CmdEnum.valueOf(cmd).getEventName());
            if (null == event) {
                throw new UnsupportedOperationException(String.format("cmd:%s Unsupported.", cmd));
            }

            event.onEvent(ctx, message);
        }
    }

}
