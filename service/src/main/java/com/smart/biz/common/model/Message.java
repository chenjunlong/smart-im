package com.smart.biz.common.model;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author chenjunlong
 */
@Getter
@Builder
public class Message extends BaseModel {

    private int cmd;
    private Body body;

    @Builder
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Body extends BaseModel {
        @SerializedName("sender_id")
        private long senderId;
        @SerializedName("receive_id")
        private String receiveId;
        @SerializedName("msg_type")
        private int msgType;
        @SerializedName("content")
        private String content;
        @SerializedName("timestamp")
        private long timestamp;
    }

}
