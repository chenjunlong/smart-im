package com.smart.service.biz;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.smart.data.redis.RedisTemplate;
import com.smart.service.config.RedisConfig;

/**
 * @author chenjunlong
 */
@Service
public class ConnectService {

    @Resource(name = "smartImRedisTemplate")
    private RedisTemplate redisTemplate;


    public Optional<Long> register(String address) {
        Long result = redisTemplate.sadd(RedisConfig.Key.TCP_SERVER_NODE_ADDRESS, address);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public Optional<Long> unregister(String address) {
        Long result = redisTemplate.srem(RedisConfig.Key.TCP_SERVER_NODE_ADDRESS, address);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public List<String> getConnectAddress() {
        Set<String> address = redisTemplate.smembers(RedisConfig.Key.TCP_SERVER_NODE_ADDRESS);
        if (CollectionUtils.isEmpty(address)) {
            return Collections.emptyList();
        }
        return address.stream().collect(Collectors.toList());
    }
}
