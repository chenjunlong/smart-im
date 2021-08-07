package com.smart.server.udp.handler;

import com.smart.biz.common.model.Message;
import com.smart.server.common.constant.Constant;
import com.smart.server.service.ChannelService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;


/**
 * @author chenjunlong
 */
@Slf4j
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final int mode;
    private ChannelService channelService;

    public UdpServerHandler(ChannelService channelService, int mode) {
        this.mode = mode;
        this.channelService = channelService;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws UnsupportedEncodingException {

        ByteBuf buf = msg.content();
        int len = buf.readableBytes();
        byte[] data = new byte[len];
        buf.readBytes(data);


        Message message = Message.parseFromJson(new String(data, "UTF-8"), Message.class);
        Message.Body body = Message.Body.parseFromPb(message.getBody());
        log.info("[UdpServerHandler] cmd:{}, seq:{}, body:{}", message.getCmd(), message.getSeq(), body.toJson());


        if (mode == Constant.MODE_TCP) {
            channelService.send(message);
        }

        if(mode == Constant.MODE_WEBSOCKET) {
            channelService.sendWebSocket(message);
        }

    }
}
