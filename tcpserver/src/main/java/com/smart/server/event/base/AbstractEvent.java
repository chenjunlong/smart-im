package com.smart.server.event.base;

import com.smart.biz.common.model.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenjunlong
 */
public abstract class AbstractEvent<T> implements Event<T> {

    @Override
    public void onEvent(ChannelHandlerContext ctx, T message) {
        execute(ctx, message);
    }

    /**
     * 执行客户端事件
     * 
     * @param ctx
     * @param message
     */
    public abstract void execute(ChannelHandlerContext ctx, T message);

}
