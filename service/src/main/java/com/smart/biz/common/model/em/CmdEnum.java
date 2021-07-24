package com.smart.biz.common.model.em;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息命令ID
 * 
 * @author chenjunlong
 */
@Getter
@AllArgsConstructor
public enum CmdEnum {

    /**
     * 客户端心跳消息
     */
    HEART_BEAT(0, "heartBeatEvent"),
    /**
     * 长连接认证消息
     */
    AUTH(1, "authEvent"),
    /**
     * 长连接关闭消息
     */
    CLOSED(2, "closedEvent"),
    /**
     * 长连接限流
     */
    CONNECT_REFUSED_LIMIT(3, ""),
    /**
     * 业务自定义消息
     */
    BIZ_CUSTOM(100, ""),
    /**
     * 评论消息
     */
    COMMENT(101, ""),
    /**
     * 系统消息
     */
    SYSTEM(102, "");


    private final int cmdId;
    private final String eventName;

    private static final Map<Integer, CmdEnum> enumCache = Arrays.stream(CmdEnum.values()).collect(Collectors.toMap(CmdEnum::getCmdId, Function.identity()));

    public static CmdEnum valueOf(int value) {
        return enumCache.get(value);
    }

    public static boolean isValid(int cmd) {
        return valueOf(cmd) == null ? false : true;
    }


}
