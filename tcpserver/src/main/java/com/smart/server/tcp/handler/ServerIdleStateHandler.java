package com.smart.server.tcp.handler;

import com.smart.biz.common.model.em.CloseTypeEnum;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.server.service.ChannelService;
import com.smart.server.tcp.codec.CodecObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
public class ServerIdleStateHandler extends ChannelInboundHandlerAdapter {

    private ChannelService channelService;

    public ServerIdleStateHandler(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {


                CodecObject response = new CodecObject();
                response.cmd = CmdEnum.CLOSED.getCmdId();
                response.seq = System.nanoTime();
                ctx.channel().writeAndFlush(response);


                channelService.disconnect(ctx, CloseTypeEnum.HEART_BEAT_TIMEOUT.getType());
            }
        }
    }
}
