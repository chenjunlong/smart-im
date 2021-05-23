package com.smart.service.connet;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author chenjunlong
 */
@Service
public class ConnectProxy {

    @Resource
    private RedisConnectService redisConnectService;
    @Resource
    private ZKConnectService zkConnectService;

    public Optional<Boolean> register(String address) {
        Optional<Boolean> zkResult = zkConnectService.register(address);
        Optional<Boolean> redisResult = redisConnectService.register(address);
        return (zkResult.isPresent() && redisResult.isPresent()) ? Optional.of(true) : Optional.empty();
    }

    public Optional<Boolean> unregister(String address) {
        Optional<Boolean> zkResult = zkConnectService.unregister(address);
        Optional<Boolean> redisResult = redisConnectService.unregister(address);
        return (zkResult.isPresent() && redisResult.isPresent()) ? Optional.of(true) : Optional.empty();
    }

    public List<String> getConnectAddress() {
        List<String> address = zkConnectService.getConnectAddress();
        if (CollectionUtils.isEmpty(address)) {
            return redisConnectService.getConnectAddress();
        }
        return address;
    }

}
