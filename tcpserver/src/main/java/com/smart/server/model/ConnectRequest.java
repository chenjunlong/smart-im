package com.smart.server.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author chenjunlong
 */
@Setter
@Getter
public class ConnectRequest {

    private String roomId;
    private long uid;
    private int behavior;
    private String msg;

}
