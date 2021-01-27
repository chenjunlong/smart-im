package com.smart.service.biz;

import com.smart.service.common.kafka.Topic;
import com.smart.service.common.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * @author chenjunlong
 */
@Slf4j(topic = "sendMessage")
@Service
public class MessageService {

    @Resource(name = "smartImKafkaProducer")
    private Producer producer;

    public void send(long uid, String roomId, String content) {
        Message message = new Message();
        message.setTimestamp(System.currentTimeMillis());
        message.setSender(uid);
        message.setTargetRoomId(roomId);
        message.setContent(content);
        String messageJson = message.toJson();
        Future<RecordMetadata> future = producer.send(new ProducerRecord(Topic.SMART_IM_MESSAGE, messageJson));
        try {
            future.get();
            log.info(messageJson);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
