package com.smart.tcp.handler;

import com.smart.biz.common.model.Message;
import com.smart.biz.common.model.em.CloseTypeEnum;
import com.smart.biz.common.model.em.CmdEnum;
import com.smart.server.service.ChannelService;

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

                // 下推心跳超时断开连接消息
                Message push = Message.builder().build();
                push.setCmd(CmdEnum.CLOSED.getCmdId());
                push.setSeq(System.nanoTime());
                ctx.channel().writeAndFlush(push);


                // 断开连接
                channelService.disconnect(ctx.channel(), CloseTypeEnum.HEART_BEAT_TIMEOUT.getType());

            }
        }

    }
}
