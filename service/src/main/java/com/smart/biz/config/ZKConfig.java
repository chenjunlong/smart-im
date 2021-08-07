package com.smart.biz.config;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author chenjunlong
 */
@Configuration
public class ZKConfig {

    @Value("${zookeeper.smart-im.address}")
    private String host;

    @Bean
    @Lazy
    public ZkClient smartImZkClient() {
        return new ZkClient(host, 10000);
    }

}
