package com.smart.server.common.constant;

import com.smart.server.common.utils.NetWorkUtils;

/**
 * @author chenjunlong
 */
public class Constant {

    /**
     * 本机IP
     */
    public static final String LOCAL_IP;

    /**
     * 模式1TCP、2WebSocket
     */
    public static final int MODE_TCP = 1;
    public static final int MODE_WEBSOCKET = 2;

    static {
        LOCAL_IP = NetWorkUtils.getLocalIp();
    }

}
