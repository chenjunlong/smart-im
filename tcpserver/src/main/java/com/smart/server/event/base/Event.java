package com.smart.server.event.base;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenjunlong
 */
public interface Event<T> {

    /**
     * 事件处理器
     *
     * @param ctx
     * @param message
     */
    void onEvent(ChannelHandlerContext ctx, T message);

}
