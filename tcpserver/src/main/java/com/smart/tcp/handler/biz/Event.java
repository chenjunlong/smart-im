package com.smart.tcp.handler.biz;

import com.smart.tcp.codec.CodecObject;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenjunlong
 */
public interface Event {

    /**
     * 事件处理器
     *
     * @param ctx
     * @param codecObject
     */
    void onEvent(ChannelHandlerContext ctx, CodecObject codecObject);

}
