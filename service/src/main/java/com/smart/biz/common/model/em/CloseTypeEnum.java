package com.smart.biz.common.model.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author chenjunlong
 */
@Getter
@AllArgsConstructor
public enum CloseTypeEnum {

    /**
     * 客户端主动关闭
     */
    NORMAL(1),
    /**
     * 心跳超时关闭
     */
    HEART_BEAT_TIMEOUT(2),
    /**
     * 客户端断开
     */
    CLIENT(3);

    private final int type;
}
