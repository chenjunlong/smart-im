package com.smart.service.biz;

import com.smart.data.redis.RedisTemplate;
import com.smart.service.config.RedisConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        Long result = redisTemplate.sadd(RedisConfig.Key.TCP_SERVER_NODE_ADDRESS, address);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public Set<String> getConnectAddress() {
        Set<String> address = redisTemplate.smembers(RedisConfig.Key.TCP_SERVER_NODE_ADDRESS);
        if(CollectionUtils.isEmpty(address)) {
            return Collections.emptySet();
        }
        return address;
    }
}
