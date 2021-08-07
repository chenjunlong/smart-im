package com.smart.api.service;

import java.util.List;

import javax.annotation.Resource;

import com.smart.biz.registry.RegistryProxy;
import org.springframework.stereotype.Service;

import com.smart.api.address.LoadBalanceStrategy;

/**
 * @author chenjunlong
 */
@Service
public class DispatchService {

    @Resource(name = "tcpRegistryProxy")
    private RegistryProxy tcpRegistryProxy;
    @Resource(name = "wsRegistryProxy")
    private RegistryProxy wsRegistryProxy;
    @Resource
    private LoadBalanceStrategy loadBalanceStrategy;


    public List<String> getAddress() {
        List<String> address = tcpRegistryProxy.getConnectAddress();
        return loadBalanceStrategy.getLoadBalance().select(address);
    }

    public List<String> getWsAddress() {
        List<String> address = wsRegistryProxy.getConnectAddress();
        return loadBalanceStrategy.getLoadBalance().select(address);
    }

}
