package com.smart.api.address;

import java.util.List;

/**
 * TcpServer负载均衡策略
 * 
 * @author chenjunlong
 */
public interface LoadBalance {

    List<String> select(List<String> address);
}
