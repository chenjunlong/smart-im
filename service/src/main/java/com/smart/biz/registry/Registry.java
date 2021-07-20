package com.smart.biz.registry;

import java.util.List;
import java.util.Optional;

/**
 * @author chenjunlong
 */
public interface Registry {

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

    /**
     * 发送心跳
     * 
     * @param address
     */
    void beatHeart(String address);
}
