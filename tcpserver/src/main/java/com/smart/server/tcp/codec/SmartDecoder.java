package com.smart.server.tcp.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author chenjunlong
 */
public class SmartDecoder extends LengthFieldBasedFrameDecoder {

    public SmartDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) {
        if (in == null) {
            return null;
        }

        CodecObject codecObject = new CodecObject();
        codecObject.cmd = in.readInt();
        codecObject.seq = in.readLong();
        int bodyLen = in.readInt();

        if (bodyLen > 0) {
            codecObject.body = new byte[bodyLen];
            in.readBytes(codecObject.body);
        }
        return codecObject;
    }
}
