package com.smart.server.udp;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.smart.biz.registry.RegistryProxy;
import com.smart.server.common.constant.Constant;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class UdpRegistry {

    private ScheduledExecutorService scheduledExecutorService =
            new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("UdpRegistry-schedule-pool-%d").daemon(true).build());


    @Value("${udpserver.port}")
    private int port;

    @Resource(name = "udpRegistryProxy")
    private RegistryProxy registryProxy;


    public UdpRegistry() {
        // 每10s发送心跳包到zk
        this.scheduledExecutorService.scheduleAtFixedRate(() -> registryProxy.beatHeart(getAddress()), 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 服务注册到zk
     * 
     * @return
     */
    public boolean register() {
        return registryProxy.register(getAddress()).isPresent();
    }

    /**
     * 从zk删除服务节点
     * 
     * @return
     */
    public boolean unregister() {
        return registryProxy.unregister(getAddress()).isPresent();
    }


    private String getAddress() {
        return StringUtils.joinWith(":", Constant.LOCAL_IP, port);
    }
}
