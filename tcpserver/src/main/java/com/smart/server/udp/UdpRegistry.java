package com.smart.server.udp;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.smart.biz.registry.RegistryProxy;
import com.smart.server.common.constant.Constant;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class UdpRegistry {

    @Value("${udpserver.port}")
    private int port;

    @Resource(name = "udpRegistryProxy")
    private RegistryProxy registryProxy;


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
