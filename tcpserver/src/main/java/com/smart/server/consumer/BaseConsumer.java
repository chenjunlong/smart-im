package com.smart.server.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenjunlong
 */
@Slf4j(topic = "kafka")
public abstract class BaseConsumer<K, V> {

    /**
     * 配置topic
     *
     * @return
     */
    public abstract String topic();

    /**
     * 配置groupId
     *
     * @return
     */
    public abstract String groupId();

    /**
     * 消费端配置信息
     *
     * @return
     */
    public abstract Properties consumerConfig();

    /**
     * 线程池配置
     *
     * @return
     */
    public abstract ExecutorService getThreadPool();

    /**
     * 业务处理
     *
     * @param consumerRecord 消息体
     */
    public abstract void doExecute(ConsumerRecord<K, V> consumerRecord);

    private static final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 100L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    @PostConstruct
    public void execute() {
        Properties props = this.consumerConfig();
        props.put("group.id", this.groupId());

        KafkaConsumer<K, V> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(this.topic()));

        executorService.execute(() -> {
            while (true) {
                ConsumerRecords<K, V> records = consumer.poll(Duration.ofSeconds(100L));
                for (ConsumerRecord<K, V> consumerRecord : records) {
                    this.getThreadPool().execute(() -> {
                        try {
                            doExecute(consumerRecord);
                        } catch (Exception e) {
                            log.error(String.format("[BaseConsumer] poll failure, topic: %s", topic()), e);
                        }
                    });
                }
            }
        });

    }
}
