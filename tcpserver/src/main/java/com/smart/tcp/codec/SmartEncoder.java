package com.smart.tcp.codec;

import com.smart.biz.common.model.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author chenjunlong
 */
public class SmartEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) {

        out.writeInt(msg.getVersion());
        out.writeInt(msg.getCmd());
        out.writeLong(msg.getSeq());
        out.writeInt(null != msg.getBody() ? msg.getBody().length : 0);

        if (null != msg.getBody()) {
            out.writeBytes(msg.getBody());
        }
    }
}
