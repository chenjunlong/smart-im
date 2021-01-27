package com.smart.server.tcp.codec;

import com.google.gson.Gson;
import com.smart.server.model.ConnectRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author chenjunlong
 */
@ChannelHandler.Sharable
public class JsonDecoder extends MessageToMessageDecoder<ByteBuf> {

    private final Charset charset;
    private final Gson gson = new Gson();

    /**
     * Creates a new instance with the current system character set.
     */
    public JsonDecoder() {
        this(Charset.defaultCharset());
    }

    /**
     * Creates a new instance with the specified character set.
     */
    public JsonDecoder(Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        try {
            String content = msg.toString(charset);
            ConnectRequest connectRequest = gson.fromJson(content, ConnectRequest.class);
            out.add(connectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
