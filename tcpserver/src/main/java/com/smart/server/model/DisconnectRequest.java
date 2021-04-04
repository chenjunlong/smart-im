package com.smart.server.model;

import com.smart.service.common.model.BaseModel;

import lombok.Data;

/**
 * @author chenjunlong
 */
@Data
public class DisconnectRequest extends BaseModel {

    private String roomId;
    private long uid;

}