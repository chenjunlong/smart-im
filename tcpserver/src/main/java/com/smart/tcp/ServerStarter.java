package com.smart.tcp;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.smart.server.service.ChannelService;
import com.smart.server.udp.UdpRegistry;
import com.smart.server.udp.UdpServer;
import com.smart.tcp.handler.ServerChannelHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class ServerStarter implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${tcpserver.port}")
    private int port;
    @Value("${tcpserver.boss-threads}")
    private int bossThreads;
    @Value("${tcpserver.worker-threads}")
    private int workerThreads;
    @Value("${udpserver.port}")
    private int udpPort;

    @Resource
    private ServerChannelHandler serverChannelHandler;
    @Resource
    private TcpRegistry tcpRegistry;
    @Resource
    private UdpRegistry udpRegistry;
    @Resource
    private ChannelService channelService;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        UdpServer udpServer = new UdpServer(udpPort, udpRegistry, channelService);
        udpServer.start();

        TcpServer tcpServer = new TcpServer(port, bossThreads, workerThreads, serverChannelHandler, tcpRegistry, channelService);
        tcpServer.start();
    }

}
