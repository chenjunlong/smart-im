package com.smart.ws;

import javax.annotation.Resource;

import com.smart.server.common.constant.Constant;
import com.smart.ws.handler.WsServerChannelHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.smart.server.service.ChannelService;
import com.smart.server.tcp.register.ServerRegistry;
import com.smart.server.udp.UdpRegistry;
import com.smart.server.udp.UdpServer;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class WsServerStarer implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${wsserver.port}")
    private int port;
    @Value("${wsserver.boss-threads}")
    private int bossThreads;
    @Value("${wsserver.worker-threads}")
    private int workerThreads;
    @Value("${udpserver.port}")
    private int udpPort;

    @Resource
    private UdpRegistry udpRegistry;
    @Resource
    private ChannelService channelService;
    @Resource(name = "wsServerRegistry")
    private ServerRegistry serverRegistry;
    @Resource
    private WsServerChannelHandler wsServerChannelHandler;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        UdpServer udpServer = new UdpServer(udpPort, udpRegistry, channelService, Constant.MODE_WEBSOCKET);
        udpServer.start();

        WsServer wsServer = new WsServer(port, bossThreads, workerThreads, wsServerChannelHandler, serverRegistry, channelService);
        wsServer.start();

    }

}
