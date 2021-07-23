package com.smart.biz.config;

import lombok.Setter;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author chenjunlong
 * https://cwiki.apache.org/confluence/display/KAFKA/KIP-19+-+Add+a+request+timeout+to+NetworkClient
 */
@Setter
@Configuration
public class KafkaConfig {

    @Value("${kafka.smart-im.bootstrap-server}")
    private String smartImBootstrapServer;

    @Bean
    public Properties smartImKafkaProducerConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.smartImBootstrapServer);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000); // 客户端等待时长
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000); // 生产者阻止缓冲时间，序列化时间
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    @Bean
    public KafkaProducer<String, String> smartImKafkaProducer(@Qualifier("smartImKafkaProducerConfig") Properties props) {
        return new KafkaProducer(props);
    }
}
