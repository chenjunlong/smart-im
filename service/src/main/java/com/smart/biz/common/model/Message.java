package com.smart.biz.common.model;

import com.google.gson.annotations.SerializedName;
import com.smart.biz.common.model.proto.wrap.MessageWrap;

import lombok.*;

/**
 * @author chenjunlong
 */
@Data
@Builder
public class Message extends BaseModel {

    private int version = 1;
    private int cmd;
    private long seq;
    private byte[] body;

    @Data
    @Builder
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

        public MessageWrap.Body toPb() {

            MessageWrap.Body.Builder body = MessageWrap.Body.newBuilder();

            body.setSenderId(this.senderId);
            body.setReceiveId(this.receiveId);
            body.setMsgType(this.msgType);
            body.setContent(this.content);
            body.setTimestamp(this.timestamp);

            return body.build();
        }

        public byte[] toPbBytes() {
            return this.toPb().toByteArray();
        }

        public static Body parseFromPb(MessageWrap.Body bodyWrap) {

            if (bodyWrap == null) {
                return null;
            }

            Body body = Body.builder().build();
            body.senderId = bodyWrap.getSenderId();
            body.receiveId = bodyWrap.getReceiveId();
            body.msgType = bodyWrap.getMsgType();
            body.content = bodyWrap.getContent();
            body.timestamp = bodyWrap.getTimestamp();

            return body;
        }

        @SneakyThrows
        public static Body parseFromPb(byte[] bytes) {

            if (bytes == null) {
                return null;
            }

            MessageWrap.Body bodyWrap = MessageWrap.Body.parseFrom(bytes);
            if (bodyWrap == null) {
                return null;
            }

            return parseFromPb(bodyWrap);
        }
    }


    @Data
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class Connect extends BaseModel {

        private long uid;
        private String roomId;


        public MessageWrap.Connect toPb() {

            MessageWrap.Connect.Builder connect = MessageWrap.Connect.newBuilder();
            connect.setUid(uid);
            connect.setRoomId(roomId);

            return connect.build();
        }

        public byte[] toPbBytes() {
            return this.toPb().toByteArray();
        }


        public static Connect parseFromPb(MessageWrap.Connect connectWrap) {

            if (connectWrap == null) {
                return null;
            }

            Connect connect = Connect.builder().build();
            connect.uid = connectWrap.getUid();
            connect.roomId = connectWrap.getRoomId();

            return connect;
        }


        @SneakyThrows
        public static Connect parseFromPb(byte[] bytes) {

            if (bytes == null) {
                return null;
            }

            MessageWrap.Connect connectWrap = MessageWrap.Connect.parseFrom(bytes);
            if (connectWrap == null) {
                return null;
            }

            return parseFromPb(connectWrap);
        }
    }

}
