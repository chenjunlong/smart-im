package com.smart.server.common.constant;

import com.smart.server.common.utils.NetWorkUtils;

/**
 * @author chenjunlong
 */
public class Constant {

    public static final String LOCAL_IP;

    static {
        LOCAL_IP = NetWorkUtils.getLocalIp();
    }

}
