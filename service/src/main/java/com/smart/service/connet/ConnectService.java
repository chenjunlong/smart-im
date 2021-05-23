package com.smart.service.connet;

import java.util.List;
import java.util.Optional;

/**
 * @author chenjunlong
 */
public interface ConnectService {

    /**
     * 注册节点
     * 
     * @param address
     * @return
     */
    Optional<Boolean> register(String address);

    /**
     * 取消注册
     * 
     * @param address
     * @return
     */
    Optional<Boolean> unregister(String address);

    /**
     * 获取节点地址
     * 
     * @return
     */
    List<String> getConnectAddress();
}
