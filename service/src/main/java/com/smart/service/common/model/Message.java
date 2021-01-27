package com.smart.service.common.model;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenjunlong
 */
@Setter
@Getter
public class Message {

    private long sender;
    private String targetRoomId;
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
