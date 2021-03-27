package com.smart.api.controller;

import com.smart.api.annotation.RequestLog;
import com.smart.api.annotation.ResponseLog;
import com.smart.api.auth.BaseInfo;
import com.smart.service.biz.ConnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author chenjunlong
 */
@Slf4j
@RestController
@RequestMapping("/dispatch")
public class DispatchController {

    @Resource
    private ConnectService connectService;


    /**
     * curl 'http://localhost:8000/v1/smart-im/dispatch/connect_address'
     */
    @RequestLog
    @ResponseLog
    @BaseInfo(desc = "获取Tcp服务地址", needAuth = true)
    @GetMapping(value = "/connect_address", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> connectAddress() {
        return connectService.getConnectAddress();
    }

}
