package com.smart.api.address;

import java.util.Map;

import javax.annotation.Resource;

import com.smart.api.address.LoadBalance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author chenjunlong
 */
@Component
public class LoadBalanceStrategy {

    @Value("${config.tcp-loadbalance}")
    private String strategyName;
    @Resource
    private Map<String, LoadBalance> strategyMap;


    public LoadBalance build() {
        return strategyMap.get(strategyName);
    }

}
