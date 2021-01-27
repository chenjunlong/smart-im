package com.smart.service.biz;

import com.smart.data.redis.RedisTemplate;
import com.smart.service.config.RedisConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author chenjunlong
 */
@Service
public class ConnectService {

    @Resource(name = "smartImRedisTemplate")
    private RedisTemplate redisTemplate;


    public Set<String> getConnectAddress() {
        return redisTemplate.zrange(RedisConfig.Key.TCP_SERVER_NODE_INFO_ZSET, 0, -1);
    }
}
