package com.smart.server.tcp;

import com.smart.server.tcp.handler.ChildChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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


    @Resource
    private ChildChannelHandler childChannelHandler;
    @Resource
    private ServerRegistry serverRegistry;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        NettyServer nettyServer = new NettyServer(port, bossThreads, workerThreads, childChannelHandler, serverRegistry);
        nettyServer.start();
    }

}
