package com.smart.biz.common.model.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型
 * 
 * @author chenjunlong
 */
@Getter
@AllArgsConstructor
public enum MsgTypeEnum {

    /**
     * 点对点消息
     */
    P2P(0),
    /**
     * 房间消息
     */
    ROOM(1),
    /**
     * 广播消息
     */
    BOARD_CAST(2);

    private final int type;
}
