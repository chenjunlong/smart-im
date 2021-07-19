package com.smart.service.registry.impl;

import com.smart.data.redis.RedisTemplate;
import com.smart.service.config.RedisConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author chenjunlong
 */
@Service("redisRegistry")
public class RedisRegistry extends AbstractRegistry {

    @Resource(name = "smartImRedisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public Optional<Boolean> register(String address) {
        Long result = redisTemplate.sadd(RedisConfig.Key.TCP_SERVER_NODE_ADDRESS, address);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result > 0L);
    }

    @Override
    public Optional<Boolean> unregister(String address) {
        Long result = redisTemplate.srem(RedisConfig.Key.TCP_SERVER_NODE_ADDRESS, address);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result > 0L);
    }

    @Override
    public List<String> getConnectAddress() {
        Set<String> address = redisTemplate.smembers(RedisConfig.Key.TCP_SERVER_NODE_ADDRESS);
        if (CollectionUtils.isEmpty(address)) {
            return Collections.emptyList();
        }
        return address.stream().collect(Collectors.toList());
    }

    @Override
    public void beatHeart(String address) {

    }

}
