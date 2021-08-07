package com.smart.tcp;

import javax.annotation.Resource;

import com.smart.server.common.constant.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.smart.server.service.ChannelService;
import com.smart.server.tcp.register.ServerRegistry;
import com.smart.server.udp.UdpRegistry;
import com.smart.server.udp.UdpServer;
import com.smart.tcp.handler.TcpServerChannelHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class TcpServerStarter implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${tcpserver.port}")
    private int port;
    @Value("${tcpserver.boss-threads}")
    private int bossThreads;
    @Value("${tcpserver.worker-threads}")
    private int workerThreads;
    @Value("${udpserver.port}")
    private int udpPort;

    @Resource
    private TcpServerChannelHandler tcpServerChannelHandler;
    @Resource(name="tcpServerRegistry")
    private ServerRegistry serverRegistry;
    @Resource
    private UdpRegistry udpRegistry;
    @Resource
    private ChannelService channelService;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        UdpServer udpServer = new UdpServer(udpPort, udpRegistry, channelService, Constant.MODE_TCP);
        udpServer.start();

        TcpServer tcpServer = new TcpServer(port, bossThreads, workerThreads, tcpServerChannelHandler, serverRegistry, channelService);
        tcpServer.start();
    }

}
