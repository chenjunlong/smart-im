package com.smart.server.tcp.handler.biz;

import com.smart.server.tcp.codec.CodecObject;
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
