package com.smart.benchmark.netty.model;

import lombok.Data;

/**
 * @author chenjunlong
 */
@Data
public class ConnectRequest extends BaseModel {

    private String roomId;
    private long uid;

}
