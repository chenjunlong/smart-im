package com.smart.server.tcp.codec;

import java.util.Arrays;

/**
 * @author chenjunlong
 */
public class CodecObject {

    public int cmd;
    public byte[] body;

    @Override
    public String toString() {
        return "CodecObject{" + "cmd=" + cmd + ", body=" + new String(body) + '}';
    }
}
