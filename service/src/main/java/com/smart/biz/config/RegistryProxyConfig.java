package com.smart.biz.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smart.biz.registry.RegistryProxy;
import com.smart.biz.registry.impl.ZKRegistry;
import org.springframework.context.annotation.Lazy;

/**
 * @author chenjunlong
 */
@Configuration
public class RegistryProxyConfig {

    private static final String TCP_SERVER_REGISTER_ROOT_PATH = "/tcpserver";
    private static final String WS_SERVER_REGISTER_ROOT_PATH = "/wsserver";
    private static final String UDP_SERVER_REGISTER_ROOT_PATH = "/udpserver";


    @Bean
    @Lazy
    public RegistryProxy tcpRegistryProxy(@Qualifier("smartImZkClient") ZkClient zkClient) {
        ZKRegistry zkRegistry = new ZKRegistry(TCP_SERVER_REGISTER_ROOT_PATH, zkClient);
        return new RegistryProxy(zkRegistry);
    }

    @Bean
    @Lazy
    public RegistryProxy wsRegistryProxy(@Qualifier("smartImZkClient") ZkClient zkClient) {
        ZKRegistry zkRegistry = new ZKRegistry(WS_SERVER_REGISTER_ROOT_PATH, zkClient);
        return new RegistryProxy(zkRegistry);
    }

    @Bean
    @Lazy
    public RegistryProxy udpRegistryProxy(@Qualifier("smartImZkClient") ZkClient zkClient) {
        ZKRegistry zkRegistry = new ZKRegistry(UDP_SERVER_REGISTER_ROOT_PATH, zkClient);
        return new RegistryProxy(zkRegistry);
    }

}
