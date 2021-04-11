package com.smart.api.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.api.annotation.RequestLog;
import com.smart.api.annotation.ResponseLog;
import com.smart.api.intercepter.auth.BaseInfo;
import com.smart.api.service.DispatchService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 * @desc TcpServer节点地址调度
 */
@Slf4j
@RestController
@RequestMapping("/dispatch")
public class DispatchController {

    @Resource
    private DispatchService dispatchService;


    /**
     * curl 'http://localhost:8000/v1/smart-im/dispatch/connect_address'
     */
    @RequestLog
    @ResponseLog
    @BaseInfo(desc = "获取Tcp服务地址，返回节点地址List首个元素为主节点优先连接，其余为备份", needAuth = true, rateLimit = 500)
    @GetMapping(value = "/connect_address", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> connectAddress() {
        return dispatchService.getAddress();
    }

}
