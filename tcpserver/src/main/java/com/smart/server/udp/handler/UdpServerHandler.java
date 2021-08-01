package com.smart.server.udp.handler;

import com.smart.biz.common.model.Message;
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

    private ChannelService channelService;

    public UdpServerHandler(ChannelService channelService) {
        this.channelService = channelService;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws UnsupportedEncodingException {

        ByteBuf buf = msg.content();
        int len = buf.readableBytes();
        byte[] data = new byte[len];
        buf.readBytes(data);
        String receive = new String(data, "UTF-8");

        log.info("[UdpServerHandler] msg:{}", receive);

        Message message = Message.parseFromJson(receive, Message.class);

        channelService.send(message);
    }
}
