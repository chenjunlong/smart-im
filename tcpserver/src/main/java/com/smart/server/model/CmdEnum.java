package com.smart.server.model;

/**
 * @author chenjunlong
 */
public enum CmdEnum {

    HEART_BEAT(0),
    AUTH(1),
    RESPONSE(100);

    private final int cmdId;

    CmdEnum(int cmdId) {
        this.cmdId = cmdId;
    }

    public int getCmdId() {
        return cmdId;
    }
}
