package com.smart.api.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.smart.api.address.loadbalance.LoadBalanceStrategy;
import com.smart.service.biz.ConnectService;

/**
 * @author chenjunlong
 */
@Service
public class DispatchService {

    @Resource
    private ConnectService connectService;
    @Resource
    private LoadBalanceStrategy loadBalanceStrategy;


    public List<String> getAddress() {
        return loadBalanceStrategy.build().select(connectService.getConnectAddress());
    }
}
