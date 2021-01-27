package com.smart.server.tcp;

import com.smart.data.redis.RedisTemplate;
import com.smart.server.common.constant.Constant;
import com.smart.server.tcp.channel.ChannelRegistry;
import com.smart.service.config.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class ServerRegistry {

    @Value("${tcpserver.port}")
    private int port;

    @Resource(name = "smartImRedisTemplate")
    private RedisTemplate redisTemplate;

    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);


    @PostConstruct
    public void refresh() {
        executorService.scheduleAtFixedRate(() -> {
            String address = Constant.LOCAL_IP + ":" + port;
            long connections = ChannelRegistry.getConnections();
            this.refreshConnections(connections);
            log.info(String.format("[%s] refresh complete, connections: %s", address, connections));
        }, 10, 10, TimeUnit.SECONDS);
    }


    public void register() {
        String address = Constant.LOCAL_IP + ":" + port;
        redisTemplate.zadd(RedisConfig.Key.TCP_SERVER_NODE_INFO_ZSET, 0L, address);
    }

    public void unregister() {
        String address = Constant.LOCAL_IP + ":" + port;
        redisTemplate.zrem(RedisConfig.Key.TCP_SERVER_NODE_INFO_ZSET, address);
    }

    public void refreshConnections(long connections) {
        String address = Constant.LOCAL_IP + ":" + port;
        redisTemplate.zadd(RedisConfig.Key.TCP_SERVER_NODE_INFO_ZSET, connections, address);
    }

}
