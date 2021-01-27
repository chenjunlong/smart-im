package com.smart.server.consumer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.smart.server.common.constant.Constant;
import com.smart.server.tcp.channel.ChannelRegistry;
import com.smart.service.common.kafka.Topic;
import com.smart.service.common.model.Message;
import io.netty.channel.Channel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjunlong
 */
@Component
public class MessageConsumer extends BaseConsumer<String, String> {

    private static final Gson gson = new Gson();

    private static final ExecutorService pool =
            new ThreadPoolExecutor(16, 16, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(200), new ThreadFactoryBuilder().setNameFormat("MessageConsumerPool").build(), new ThreadPoolExecutor.CallerRunsPolicy());

    @Value("${kafka.smart-im.bootstrap-server}")
    private String bootstrapServer;
    @Value("${tcpserver.port}")
    private int port;

    @Override
    public String topic() {
        return Topic.SMART_IM_MESSAGE;
    }

    @Override
    public String groupId() {
        return "tcp.node." + Constant.LOCAL_IP + "." + port;
    }

    @Override
    public Properties consumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    @Override
    public ExecutorService getThreadPool() {
        return pool;
    }

    @Override
    public void doExecute(ConsumerRecord<String, String> consumerRecord) {
        if (consumerRecord == null) {
            return;
        }

        Message message = Message.toObject(consumerRecord.value());
        Set<Long> userSet = ChannelRegistry.getUidByRoomId(message.getTargetRoomId());
        for (long uid : userSet) {
            Channel channel = ChannelRegistry.getChannelByUid(uid);
            if (channel == null) {
                continue;
            }
            channel.writeAndFlush(message);
        }
    }

}
