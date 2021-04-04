package com.smart.service.common.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenjunlong
 */
public enum CmdEnum {

    HEART_BEAT(0, "heartBeatEvent"),
    AUTH(1, "authEvent"),
    CLOSED(2, "closedEvent"),
    COMMENT(101, "");

    private static final Map<Integer, CmdEnum> enumCache = Arrays.stream(CmdEnum.values()).collect(Collectors.toMap(e -> e.getCmdId(), e -> e));

    public static CmdEnum valueOf(int value) {
        return enumCache.get(value);
    }

    public static boolean isValid(int cmd) {
        return valueOf(cmd) == null ? false : true;
    }

    private final int cmdId;
    private final String eventName;

    CmdEnum(int cmdId, String eventName) {
        this.cmdId = cmdId;
        this.eventName = eventName;
    }

    public int getCmdId() {
        return cmdId;
    }

    public String getEventName() {
        return eventName;
    }
}
