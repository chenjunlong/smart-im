package com.smart.service.common.model;

import com.google.gson.Gson;
import lombok.Data;

/**
 * @author chenjunlong
 */
@Data
public class Message {

    private long senderId;
    private String receiveId;
    private int boardCast;
    private int cmd;
    private String content;
    private long timestamp;

    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }

    public static Message toObject(String value) {
        Message message = gson.fromJson(value, Message.class);
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }
}
