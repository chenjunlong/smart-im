package com.smart.biz.common.model;


import lombok.Builder;

/**
 * 用户进出场消息
 * 
 * @author chenjunlong
 */
@Builder
public class UserInOutMessage extends BaseModel {

    private long uid;
    private String roomId;
    private int cmd;
    private int closeType;
    private long timestamp;
}
