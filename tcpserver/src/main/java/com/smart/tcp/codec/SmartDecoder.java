package com.smart.tcp.codec;

import com.smart.biz.common.model.Message;

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


        Message message = Message.builder().build();
        message.setVersion(in.readInt());
        message.setCmd(in.readInt());
        message.setSeq(in.readLong());

        int bodyLen = in.readInt();
        if (bodyLen > 0) {
            byte[] body = new byte[bodyLen];
            in.readBytes(body);
            message.setBody(body);
        }

        return message;
    }
}
