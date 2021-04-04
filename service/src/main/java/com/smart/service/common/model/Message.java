package com.smart.service.common.model;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author chenjunlong
 */
@Getter
public class Message extends BaseModel {

    private int cmd;
    private Body body;

    public Message build(int cmd, Body body) {
        this.cmd = cmd;
        this.body = body;
        return this;
    }

    @ToString
    @EqualsAndHashCode
    public static class Body extends BaseModel {
        @SerializedName("sender_id")
        public long senderId;
        @SerializedName("receive_id")
        public String receiveId;
        @SerializedName("board_cast")
        public int boardCast;
        public String content;
        public long timestamp = System.currentTimeMillis();
    }

}
