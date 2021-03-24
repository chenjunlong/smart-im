package com.smart.service.config;

import com.smart.data.redis.RedisTemplate;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author chenjunlong
 */
@Setter
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "redis.smart-im")
public class RedisConfig {

    private JedisPoolConfig jedisPoolConfig;

    @Value("${redis.smart-im.jedis-config.host}")
    private String host;
    @Value("${redis.smart-im.jedis-config.port}")
    private int port;
    @Value("${redis.smart-im.jedis-config.timeout}")
    private int timeout;

    @Bean
    public JedisPool smartImJedisPool() {
        return new JedisPool(jedisPoolConfig, host, port, timeout);
    }

    @Bean
    public RedisTemplate smartImRedisTemplate(@Qualifier("smartImJedisPool") JedisPool jedisPool) {
        return new RedisTemplate(jedisPool);
    }


    public static class Key {
        public static final String TCP_SERVER_NODE_ADDRESS = "tcp.server.node.address";
    }
}

