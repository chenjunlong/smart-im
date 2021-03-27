package com.smart.data.redis;

import redis.clients.jedis.Jedis;

/**
 * @author chenjunlong
 */
public interface RedisCallback<T> {
    T doInRedis(Jedis jedis);
}
