package com.smart.server.tcp.handler.biz;

import com.smart.server.tcp.codec.CodecObject;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author chenjunlong
 */
public abstract class AbstractEvent implements Event {

    @Override
    public void onEvent(ChannelHandlerContext ctx, CodecObject codecObject) {
        execute(ctx, codecObject);
    }

    /**
     * 执行客户端事件
     * 
     * @param ctx
     * @param codecObject
     */
    public abstract void execute(ChannelHandlerContext ctx, CodecObject codecObject);

}
