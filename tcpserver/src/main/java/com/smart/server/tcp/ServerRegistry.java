package com.smart.server.tcp;

import com.smart.server.common.constant.Constant;
import com.smart.biz.registry.RegistryProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class ServerRegistry {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Value("${tcpserver.port}")
    private int port;

    @Resource(name = "registryProxy")
    private RegistryProxy registryProxy;


    public ServerRegistry() {
        this.beatHeart();
    }

    /**
     * 每10s发送心跳包到zk
     */
    public void beatHeart() {
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String address = Constant.LOCAL_IP + ":" + port;
                registryProxy.beatHeart(address);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 服务注册到zk
     * 
     * @return
     */
    public boolean register() {
        String address = Constant.LOCAL_IP + ":" + port;
        return registryProxy.register(address).isPresent();
    }

    /**
     * 从zk删除服务节点
     * 
     * @return
     */
    public boolean unregister() {
        String address = Constant.LOCAL_IP + ":" + port;
        return registryProxy.unregister(address).isPresent();
    }

}
