package com.smart.tcp.handler.biz;

import com.smart.biz.common.model.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenjunlong
 */
public interface Event {

    /**
     * 事件处理器
     *
     * @param ctx
     * @param message
     */
    void onEvent(ChannelHandlerContext ctx, Message message);

}
