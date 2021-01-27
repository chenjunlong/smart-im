package com.smart.server.tcp.codec;

import com.smart.service.common.model.Message;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.internal.ObjectUtil;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author chenjunlong
 */
@ChannelHandler.Sharable
public class JsonEncoder extends MessageToMessageEncoder<Message> {

    private final Charset charset;

    /**
     * Creates a new instance with the current system character set.
     */
    public JsonEncoder() {
        this(Charset.defaultCharset());
    }

    /**
     * Creates a new instance with the specified character set.
     */
    public JsonEncoder(Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
        if (msg == null) {
            return;
        }

        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg.toJson()), charset));
    }
}
