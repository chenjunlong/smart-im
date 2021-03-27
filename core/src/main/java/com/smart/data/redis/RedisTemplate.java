package com.smart.data.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ListPosition;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.StreamConsumersInfo;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.StreamGroupInfo;
import redis.clients.jedis.StreamInfo;
import redis.clients.jedis.StreamPendingEntry;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author chenjunlong
 */
@Slf4j(topic = "jedis")
public class RedisTemplate implements JedisCommands {

    private final JedisPool jedisPool;
    private int retryCount = 3;
    private boolean throwException = false;

    public RedisTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public RedisTemplate(JedisPool jedisPool, int retryCount) {
        this.jedisPool = jedisPool;
        this.retryCount = retryCount;
    }

    public RedisTemplate(JedisPool jedisPool, int retryCount, boolean throwException) {
        this.jedisPool = jedisPool;
        this.retryCount = retryCount;
        this.throwException = throwException;
    }

    public <T> T execute(RedisCallback action) {
        Jedis jedis = null;
        int count = 0;
        while (count < this.retryCount) {
            try {
                jedis = this.jedisPool.getResource();
                break;
            } catch (Exception e) {
                count++;
                log.warn(String.format("[RedisTemplate] jedisPool.getResource retry: %s", count), e);
            }
        }

        if (jedis == null) {
            log.error("[RedisTemplate] jedisPool.getResource failure");
            throw new JedisDataException("jedis get failure");
        }

        Object result;
        try {
            result = action.doInRedis(jedis);
        } catch (Exception e) {
            if (throwException) {
                throw new JedisDataException(e);
            }
            return null;
        } finally {
            jedis.close();
        }

        return (T) result;
    }

    @Override
    public String set(String key, String value) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    @Override
    public String set(String key, String value, SetParams setParams) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.set(key, value, setParams);
            }
        });
    }

    @Override
    public String get(String key) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    @Override
    public Boolean exists(String key) {
        return (Boolean) this.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    @Override
    public Long persist(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.persist(key);
            }
        });
    }

    @Override
    public String type(String key) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    @Override
    public byte[] dump(String key) {
        return (byte[]) this.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(Jedis jedis) {
                return jedis.dump(key);
            }
        });
    }

    @Override
    public String restore(String key, int ttl, byte[] serializedValue) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.restore(key, ttl, serializedValue);
            }
        });
    }

    @Override
    public String restoreReplace(String key, int ttl, byte[] serializedValue) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.restoreReplace(key, ttl, serializedValue);
            }
        });
    }

    @Override
    public Long expire(String key, int seconds) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    @Override
    public Long pexpire(String key, long milliseconds) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.pexpire(key, milliseconds);
            }
        });
    }

    @Override
    public Long expireAt(String key, long unixTime) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.expireAt(key, unixTime);
            }
        });
    }

    @Override
    public Long pexpireAt(String key, long millisecondsTimestamp) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.pexpireAt(key, millisecondsTimestamp);
            }
        });
    }

    @Override
    public Long ttl(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.ttl(key);
            }
        });
    }

    @Override
    public Long pttl(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.pttl(key);
            }
        });
    }

    @Override
    public Long touch(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.touch(key);
            }
        });
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value) {
        return (Boolean) this.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
                return jedis.setbit(key, offset, value);
            }
        });
    }

    @Override
    public Boolean setbit(String key, long offset, String value) {
        return (Boolean) this.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
                return jedis.setbit(key, offset, value);
            }
        });
    }

    @Override
    public Boolean getbit(String key, long offset) {
        return (Boolean) this.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
                return jedis.getbit(key, offset);
            }
        });
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.setrange(key, offset, value);
            }
        });
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.getrange(key, startOffset, endOffset);
            }
        });
    }

    @Override
    public String getSet(String key, String value) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.getSet(key, value);
            }
        });
    }

    @Override
    public Long setnx(String key, String value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.setnx(key, value);
            }
        });
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.setex(key, seconds, value);
            }
        });
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.psetex(key, milliseconds, value);
            }
        });
    }

    @Override
    public Long decrBy(String key, long decrement) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.decrBy(key, decrement);
            }
        });
    }

    @Override
    public Long decr(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.decr(key);
            }
        });
    }

    @Override
    public Long incrBy(String key, long increment) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.incrBy(key, increment);
            }
        });
    }

    @Override
    public Double incrByFloat(String key, double increment) {
        return (Double) this.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.incrByFloat(key, increment);
            }
        });
    }

    @Override
    public Long incr(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    @Override
    public Long append(String key, String value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.append(key, value);
            }
        });
    }

    @Override
    public String substr(String key, int start, int end) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.substr(key, start, end);
            }
        });
    }

    @Override
    public Long hset(String key, String field, String value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hset(key, field, value);
            }
        });
    }

    @Override
    public Long hset(String key, Map<String, String> hash) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hset(key, hash);
            }
        });
    }

    @Override
    public String hget(String key, String field) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hsetnx(key, field, value);
            }
        });
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.hmset(key, hash);
            }
        });
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.hmget(key, fields);
            }
        });
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hincrBy(key, field, value);
            }
        });
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        return (Double) this.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.hincrByFloat(key, field, value);
            }
        });
    }

    @Override
    public Boolean hexists(String key, String field) {
        return (Boolean) this.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
                return jedis.hexists(key, field);
            }
        });
    }

    @Override
    public Long hdel(String key, String... field) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hdel(key, field);
            }
        });
    }

    @Override
    public Long hlen(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hlen(key);
            }
        });
    }

    @Override
    public Set<String> hkeys(String key) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
    }

    @Override
    public List<String> hvals(String key) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.hvals(key);
            }
        });
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return (Map<String, String>) this.execute(new RedisCallback<Map<String, String>>() {
            @Override
            public Map<String, String> doInRedis(Jedis jedis) {
                return jedis.hgetAll(key);
            }
        });
    }

    @Override
    public Long rpush(String key, String... strings) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.rpush(key, strings);
            }
        });
    }

    @Override
    public Long lpush(String key, String... strings) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.lpush(key, strings);
            }
        });
    }

    @Override
    public Long llen(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    @Override
    public List<String> lrange(String key, long start, long stop) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.lrange(key, start, stop);
            }
        });
    }

    @Override
    public String ltrim(String key, long start, long stop) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.ltrim(key, start, stop);
            }
        });
    }

    @Override
    public String lindex(String key, long index) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.lindex(key, index);
            }
        });
    }

    @Override
    public String lset(String key, long index, String value) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.lset(key, index, value);
            }
        });
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.lrem(key, count, value);
            }
        });
    }

    @Override
    public String lpop(String key) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.lpop(key);
            }
        });
    }

    @Override
    public String rpop(String key) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.lpop(key);
            }
        });
    }

    @Override
    public Long sadd(String key, String... strings) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.sadd(key, strings);
            }
        });
    }

    @Override
    public Set<String> smembers(String key) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.smembers(key);
            }
        });
    }

    @Override
    public Long srem(String key, String... strings) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.srem(key);
            }
        });
    }

    @Override
    public String spop(String key) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.spop(key);
            }
        });
    }

    @Override
    public Set<String> spop(String key, long count) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.spop(key, count);
            }
        });
    }

    @Override
    public Long scard(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.scard(key);
            }
        });
    }

    @Override
    public Boolean sismember(String key, String member) {
        return (Boolean) this.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
                return jedis.sismember(key, member);
            }
        });
    }

    @Override
    public String srandmember(String key) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.srandmember(key);
            }
        });
    }

    @Override
    public List<String> srandmember(String key, int count) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.srandmember(key, count);
            }
        });
    }

    @Override
    public Long strlen(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.strlen(key);
            }
        });
    }

    @Override
    public Long zadd(String key, double score, String member) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zadd(key, score, member);
            }
        });
    }

    @Override
    public Long zadd(String key, double score, String member, ZAddParams zAddParams) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zadd(key, score, member, zAddParams);
            }
        });
    }

    @Override
    public Long zadd(String key, Map<String, Double> map) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zadd(key, map);
            }
        });
    }

    @Override
    public Long zadd(String key, Map<String, Double> map, ZAddParams zAddParams) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zadd(key, map, zAddParams);
            }
        });
    }

    @Override
    public Set<String> zrange(String key, long start, long stop) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrange(key, start, stop);
            }
        });
    }

    @Override
    public Long zrem(String key, String... strings) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zrem(key, strings);
            }
        });
    }

    @Override
    public Double zincrby(String key, double increment, String member) {
        return (Double) this.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.zincrby(key, increment, member);
            }
        });
    }

    @Override
    public Double zincrby(String key, double increment, String member, ZIncrByParams zIncrByParams) {
        return (Double) this.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.zincrby(key, increment, member, zIncrByParams);
            }
        });
    }

    @Override
    public Long zrank(String key, String member) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zrank(key, member);
            }
        });
    }

    @Override
    public Long zrevrank(String key, String member) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zrevrank(key, member);
            }
        });
    }

    @Override
    public Set<String> zrevrange(String key, long start, long stop) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrevrange(key, start, stop);
            }
        });
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrangeWithScores(key, start, stop);
            }
        });
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrevrangeWithScores(key, start, stop);
            }
        });
    }

    @Override
    public Long zcard(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zcard(key);
            }
        });
    }

    @Override
    public Double zscore(String key, String member) {
        return (Double) this.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.zscore(key, member);
            }
        });
    }

    @Override
    public Tuple zpopmax(String key) {
        return (Tuple) this.execute(new RedisCallback<Tuple>() {
            @Override
            public Tuple doInRedis(Jedis jedis) {
                return jedis.zpopmax(key);
            }
        });
    }

    @Override
    public Set<Tuple> zpopmax(String key, int count) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zpopmax(key, count);
            }
        });
    }

    @Override
    public Tuple zpopmin(String key) {
        return (Tuple) this.execute(new RedisCallback<Tuple>() {
            @Override
            public Tuple doInRedis(Jedis jedis) {
                return jedis.zpopmin(key);
            }
        });
    }

    @Override
    public Set<Tuple> zpopmin(String key, int count) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zpopmin(key, count);
            }
        });
    }

    @Override
    public List<String> sort(String key) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.sort(key);
            }
        });
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParams) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.sort(key, sortingParams);
            }
        });
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zcount(key, min, max);
            }
        });
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zcount(key, min, max);
            }
        });
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrangeByScore(key, min, max);
            }
        });
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrangeByScore(key, min, max);
            }
        });
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByScore(key, min, max);
            }
        });
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrangeByScore(key, min, max, offset, count);
            }
        });
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByScore(key, min, max);
            }
        });
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrangeByScore(key, min, max, offset, count);
            }
        });
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByScore(key, min, max, offset, count);
            }
        });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max);
            }
        });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByScoreWithScores(key, min, max);
            }
        });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
            }
        });
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByScore(key, min, max, offset, count);
            }
        });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max);
            }
        });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByScoreWithScores(key, max, min);
            }
        });
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
            }
        });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
            }
        });
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return (Set<Tuple>) this.execute(new RedisCallback<Set<Tuple>>() {
            @Override
            public Set<Tuple> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
            }
        });
    }

    @Override
    public Long zremrangeByRank(String key, long start, long stop) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zremrangeByRank(key, start, stop);
            }
        });
    }

    @Override
    public Long zremrangeByScore(String key, double min, double max) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zremrangeByScore(key, min, max);
            }
        });
    }

    @Override
    public Long zremrangeByScore(String key, String min, String max) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zremrangeByScore(key, min, max);
            }
        });
    }

    @Override
    public Long zlexcount(String key, String min, String max) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zlexcount(key, min, max);
            }
        });
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrangeByLex(key, min, max);
            }
        });
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrangeByLex(key, min, max, offset, count);
            }
        });
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByLex(key, min, max);
            }
        });
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return (Set<String>) this.execute(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.zrevrangeByLex(key, min, max, offset, count);
            }
        });
    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.zremrangeByLex(key, min, max);
            }
        });
    }

    @Override
    public Long linsert(String key, ListPosition where, String pivot, String value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.linsert(key, where, pivot, value);
            }
        });
    }

    @Override
    public Long lpushx(String key, String... string) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.lpushx(key, string);
            }
        });
    }

    @Override
    public Long rpushx(String key, String... strings) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.rpushx(key, strings);
            }
        });
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.blpop(timeout, key);
            }
        });
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.brpop(timeout, key);
            }
        });
    }

    @Override
    public Long del(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.del(key);
            }
        });
    }

    @Override
    public Long unlink(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.unlink(key);
            }
        });
    }

    @Override
    public String echo(String key) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.echo(key);
            }
        });
    }

    @Override
    public Long move(String key, int dbIndex) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.move(key, dbIndex);
            }
        });
    }

    @Override
    public Long bitcount(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.bitcount(key);
            }
        });
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.bitcount(key, start, end);
            }
        });
    }

    @Override
    public Long bitpos(String key, boolean value) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.bitpos(key, value);
            }
        });
    }

    @Override
    public Long bitpos(String key, boolean value, BitPosParams params) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.bitpos(key, value);
            }
        });
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        return (ScanResult<Map.Entry<String, String>>) this.execute(new RedisCallback<ScanResult<Map.Entry<String, String>>>() {
            @Override
            public ScanResult<Map.Entry<String, String>> doInRedis(Jedis jedis) {
                return jedis.hscan(key, cursor);
            }
        });
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        return (ScanResult<Map.Entry<String, String>>) this.execute(new RedisCallback<ScanResult<Map.Entry<String, String>>>() {
            @Override
            public ScanResult<Map.Entry<String, String>> doInRedis(Jedis jedis) {
                return jedis.hscan(key, cursor);
            }
        });
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor) {
        return (ScanResult<String>) this.execute(new RedisCallback<ScanResult<String>>() {
            @Override
            public ScanResult<String> doInRedis(Jedis jedis) {
                return jedis.sscan(key, cursor);
            }
        });
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor) {
        return (ScanResult<Tuple>) this.execute(new RedisCallback<ScanResult<Tuple>>() {
            @Override
            public ScanResult<Tuple> doInRedis(Jedis jedis) {
                return jedis.zscan(key, cursor);
            }
        });
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        return (ScanResult<Tuple>) this.execute(new RedisCallback<ScanResult<Tuple>>() {
            @Override
            public ScanResult<Tuple> doInRedis(Jedis jedis) {
                return jedis.zscan(key, cursor, params);
            }
        });
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        return (ScanResult<String>) this.execute(new RedisCallback<ScanResult<String>>() {
            @Override
            public ScanResult<String> doInRedis(Jedis jedis) {
                return jedis.sscan(key, cursor, params);
            }
        });
    }

    @Override
    public Long pfadd(String key, String... elements) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.pfadd(key, elements);
            }
        });
    }

    @Override
    public long pfcount(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.pfcount(key);
            }
        });
    }

    @Override
    public Long geoadd(String key, double longitude, double latitude, String member) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.geoadd(key, longitude, latitude, member);
            }
        });
    }

    @Override
    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.geoadd(key, memberCoordinateMap);
            }
        });
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        return (Double) this.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.geodist(key, member1, member2);
            }
        });
    }

    @Override
    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        return (Double) this.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.geodist(key, member1, member2, unit);
            }
        });
    }

    @Override
    public List<String> geohash(String key, String... members) {
        return (List<String>) this.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.geohash(key, members);
            }
        });
    }

    @Override
    public List<GeoCoordinate> geopos(String key, String... members) {
        return (List<GeoCoordinate>) this.execute(new RedisCallback<List<GeoCoordinate>>() {
            @Override
            public List<GeoCoordinate> doInRedis(Jedis jedis) {
                return jedis.geopos(key, members);
            }
        });
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        return (List<GeoRadiusResponse>) this.execute(new RedisCallback<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> doInRedis(Jedis jedis) {
                return jedis.georadius(key, longitude, latitude, radius, unit);
            }
        });
    }

    @Override
    public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        return (List<GeoRadiusResponse>) this.execute(new RedisCallback<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> doInRedis(Jedis jedis) {
                return jedis.georadiusReadonly(key, longitude, latitude, radius, unit);
            }
        });
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return (List<GeoRadiusResponse>) this.execute(new RedisCallback<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> doInRedis(Jedis jedis) {
                return jedis.georadius(key, longitude, latitude, radius, unit, param);
            }
        });
    }

    @Override
    public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return (List<GeoRadiusResponse>) this.execute(new RedisCallback<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> doInRedis(Jedis jedis) {
                return jedis.georadiusReadonly(key, longitude, latitude, radius, unit, param);
            }
        });
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
        return (List<GeoRadiusResponse>) this.execute(new RedisCallback<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> doInRedis(Jedis jedis) {
                return jedis.georadiusByMember(key, member, radius, unit);
            }
        });
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit) {
        return (List<GeoRadiusResponse>) this.execute(new RedisCallback<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> doInRedis(Jedis jedis) {
                return jedis.georadiusByMemberReadonly(key, member, radius, unit);
            }
        });
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return (List<GeoRadiusResponse>) this.execute(new RedisCallback<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> doInRedis(Jedis jedis) {
                return jedis.georadiusByMember(key, member, radius, unit, param);
            }
        });
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return (List<GeoRadiusResponse>) this.execute(new RedisCallback<List<GeoRadiusResponse>>() {
            @Override
            public List<GeoRadiusResponse> doInRedis(Jedis jedis) {
                return jedis.georadiusByMemberReadonly(key, member, radius, unit, param);
            }
        });
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        return (List<Long>) this.execute(new RedisCallback<List<Long>>() {
            @Override
            public List<Long> doInRedis(Jedis jedis) {
                return jedis.bitfield(key, arguments);
            }
        });
    }

    @Override
    public List<Long> bitfieldReadonly(String key, String... arguments) {
        return (List<Long>) this.execute(new RedisCallback<List<Long>>() {
            @Override
            public List<Long> doInRedis(Jedis jedis) {
                return jedis.bitfieldReadonly(key, arguments);
            }
        });
    }

    @Override
    public Long hstrlen(String key, String field) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hstrlen(key, field);
            }
        });
    }

    @Override
    public StreamEntryID xadd(String key, StreamEntryID id, Map<String, String> hash) {
        return (StreamEntryID) this.execute(new RedisCallback<StreamEntryID>() {
            @Override
            public StreamEntryID doInRedis(Jedis jedis) {
                return jedis.xadd(key, id, hash);
            }
        });
    }

    @Override
    public StreamEntryID xadd(String key, StreamEntryID id, Map<String, String> hash, long maxLen, boolean approximateLength) {
        return (StreamEntryID) this.execute(new RedisCallback<StreamEntryID>() {
            @Override
            public StreamEntryID doInRedis(Jedis jedis) {
                return jedis.xadd(key, id, hash, maxLen, approximateLength);
            }
        });
    }

    @Override
    public Long xlen(String key) {
        return (Long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.xlen(key);
            }
        });
    }

    @Override
    public List<StreamEntry> xrange(String key, StreamEntryID start, StreamEntryID end, int count) {
        return (List<StreamEntry>) this.execute(new RedisCallback<List<StreamEntry>>() {
            @Override
            public List<StreamEntry> doInRedis(Jedis jedis) {
                return jedis.xrange(key, start, end, count);
            }
        });
    }

    @Override
    public List<StreamEntry> xrevrange(String key, StreamEntryID end, StreamEntryID start, int count) {
        return (List<StreamEntry>) this.execute(new RedisCallback<List<StreamEntry>>() {
            @Override
            public List<StreamEntry> doInRedis(Jedis jedis) {
                return jedis.xrevrange(key, start, end, count);
            }
        });
    }

    @Override
    public long xack(String key, String group, StreamEntryID... ids) {
        return (long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.xack(key, group, ids);
            }
        });
    }

    @Override
    public String xgroupCreate(String key, String groupname, StreamEntryID id, boolean makeStream) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.xgroupCreate(key, groupname, id, makeStream);
            }
        });
    }

    @Override
    public String xgroupSetID(String key, String groupname, StreamEntryID id) {
        return (String) this.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.xgroupSetID(key, groupname, id);
            }
        });
    }

    @Override
    public long xgroupDestroy(String key, String groupname) {
        return (long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.xgroupDestroy(key, groupname);
            }
        });
    }

    @Override
    public Long xgroupDelConsumer(String key, String groupname, String consumername) {
        return (long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.xgroupDelConsumer(key, groupname, consumername);
            }
        });
    }

    @Override
    public List<StreamPendingEntry> xpending(String key, String groupname, StreamEntryID start, StreamEntryID end, int count, String consumername) {
        return (List<StreamPendingEntry>) this.execute(new RedisCallback<List<StreamPendingEntry>>() {
            @Override
            public List<StreamPendingEntry> doInRedis(Jedis jedis) {
                return jedis.xpending(key, groupname, start, end, count, consumername);
            }
        });
    }

    @Override
    public long xdel(String key, StreamEntryID... ids) {
        return (long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.xdel(key, ids);
            }
        });
    }

    @Override
    public long xtrim(String key, long maxLen, boolean approximate) {
        return (long) this.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.xtrim(key, maxLen, approximate);
            }
        });
    }

    @Override
    public List<StreamEntry> xclaim(String key, String group, String consumername, long minIdleTime, long newIdleTime, int retries, boolean force, StreamEntryID... ids) {
        return (List<StreamEntry>) this.execute(new RedisCallback<List<StreamEntry>>() {
            @Override
            public List<StreamEntry> doInRedis(Jedis jedis) {
                return jedis.xclaim(key, group, consumername, minIdleTime, newIdleTime, retries, force, ids);
            }
        });
    }

    @Override
    public StreamInfo xinfoStream(String key) {
        return (StreamInfo) this.execute(new RedisCallback<StreamInfo>() {
            @Override
            public StreamInfo doInRedis(Jedis jedis) {
                return jedis.xinfoStream(key);
            }
        });
    }

    @Override
    public List<StreamGroupInfo> xinfoGroup(String key) {
        return (List<StreamGroupInfo>) this.execute(new RedisCallback<List<StreamGroupInfo>>() {
            @Override
            public List<StreamGroupInfo> doInRedis(Jedis jedis) {
                return jedis.xinfoGroup(key);
            }
        });
    }

    @Override
    public List<StreamConsumersInfo> xinfoConsumers(String key, String group) {
        return (List<StreamConsumersInfo>) this.execute(new RedisCallback<List<StreamConsumersInfo>>() {
            @Override
            public List<StreamConsumersInfo> doInRedis(Jedis jedis) {
                return jedis.xinfoConsumers(key, group);
            }
        });
    }
}
