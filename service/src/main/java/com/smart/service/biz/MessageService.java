package com.smart.service.biz;

import com.smart.service.common.kafka.Topic;
import com.smart.service.common.model.Message;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.Future;

/**
 * @author chenjunlong
 */
@Service
public class MessageService {

    private Logger log = LoggerFactory.getLogger("send_kafka_message");

    @Resource(name = "smartImKafkaProducer")
    private Producer producer;

    public void send(long senderId, String receiveId, int boardCast, int cmd, String content) {
        Message message = new Message();
        message.setTimestamp(System.currentTimeMillis());
        message.setSenderId(senderId);
        message.setReceiveId(receiveId);
        message.setCmd(cmd);
        message.setBoardCast(boardCast);
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
