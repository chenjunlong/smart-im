package com.smart.server.model;

import com.smart.biz.common.model.BaseModel;

import lombok.Data;

/**
 * @author chenjunlong
 */
@Data
public class ConnectRequest extends BaseModel {

    private String roomId;
    private long uid;

}
