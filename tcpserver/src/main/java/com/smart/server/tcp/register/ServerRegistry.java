package com.smart.server.tcp.register;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import com.smart.biz.registry.RegistryProxy;
import com.smart.server.common.constant.Constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
public class ServerRegistry {

    private ScheduledExecutorService scheduledExecutorService =
            new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder().namingPattern("ServerRegistry-schedule-pool-%d").daemon(true).build());

    private int port;
    private RegistryProxy registryProxy;

    public ServerRegistry(RegistryProxy registryProxy, int port) {
        this.port = port;
        this.registryProxy = registryProxy;

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
