package com.smart.server.tcp;

import com.smart.server.common.constant.Constant;
import com.smart.service.biz.ConnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author chenjunlong
 */
@Slf4j
@Component
public class ServerRegistry {

    @Value("${tcpserver.port}")
    private int port;

    @Resource(name = "connectService")
    private ConnectService connectService;

    public boolean register() {
        String address = Constant.LOCAL_IP + ":" + port;
        return connectService.register(address).isPresent();
    }

    public boolean unregister() {
        String address = Constant.LOCAL_IP + ":" + port;
        return connectService.unregister(address).isPresent();
    }

}
