package com.smart.api.service;

import java.util.List;

import javax.annotation.Resource;

import com.smart.service.registry.RegistryProxy;
import org.springframework.stereotype.Service;

import com.smart.api.address.LoadBalanceStrategy;

/**
 * @author chenjunlong
 */
@Service
public class DispatchService {

    @Resource
    private RegistryProxy registryProxy;
    @Resource
    private LoadBalanceStrategy loadBalanceStrategy;


    public List<String> getAddress() {
        List<String> address = registryProxy.getConnectAddress();
        return loadBalanceStrategy.getLoadBalance().select(address);
    }
}
