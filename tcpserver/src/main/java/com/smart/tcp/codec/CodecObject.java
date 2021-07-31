package com.smart.tcp.codec;

/**
 * @author chenjunlong
 */
public class CodecObject {

    public int cmd;
    public long seq;
    public byte[] body;

    @Override
    public String toString() {
        return "CodecObject{" + "cmd=" + cmd + ", seq=" + seq + ", body=" + new String(body) + '}';
    }
}
