package com.smart.biz.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.smart.data.redis.RedisTemplate;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    public JedisPool smartImJedisPool() {
        return new JedisPool(jedisPoolConfig, host, port, timeout);
    }

    @Bean
    @Lazy
    public RedisTemplate smartImRedisTemplate(@Qualifier("smartImJedisPool") JedisPool jedisPool) {
        return new RedisTemplate(jedisPool);
    }

}

