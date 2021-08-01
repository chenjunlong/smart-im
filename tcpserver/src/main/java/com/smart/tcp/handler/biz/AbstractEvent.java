package com.smart.tcp.handler.biz;

import com.smart.biz.common.model.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenjunlong
 */
public abstract class AbstractEvent implements Event {

    @Override
    public void onEvent(ChannelHandlerContext ctx, Message message) {
        execute(ctx, message);
    }

    /**
     * 执行客户端事件
     * 
     * @param ctx
     * @param message
     */
    public abstract void execute(ChannelHandlerContext ctx, Message message);

}
