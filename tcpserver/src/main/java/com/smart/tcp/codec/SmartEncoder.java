package com.smart.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author chenjunlong
 */
public class SmartEncoder extends MessageToByteEncoder<CodecObject> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CodecObject msg, ByteBuf out) {
        out.writeInt(msg.cmd);
        out.writeLong(msg.seq);
        out.writeInt(null != msg.body ? msg.body.length : 0);
        if (null != msg.body) {
            out.writeBytes(msg.body);
        }
    }
}
