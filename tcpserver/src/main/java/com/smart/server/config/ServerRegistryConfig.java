package com.smart.server.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smart.biz.registry.RegistryProxy;
import com.smart.server.tcp.register.ServerRegistry;
import org.springframework.context.annotation.Lazy;

/**
 * @author chenjunlong
 */
@Configuration
public class ServerRegistryConfig {

    @Value("${tcpserver.port}")
    private int port;
    @Value("${wsserver.port}")
    private int wsPort;


    @Bean
    @Lazy
    public ServerRegistry tcpServerRegistry(@Qualifier("tcpRegistryProxy") RegistryProxy registryProxy) {
        return new ServerRegistry(registryProxy, port);
    }

    @Bean
    @Lazy
    public ServerRegistry wsServerRegistry(@Qualifier("wsRegistryProxy") RegistryProxy registryProxy) {
        return new ServerRegistry(registryProxy, wsPort);
    }

}
