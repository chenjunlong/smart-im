package com.smart.api.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smart.api.annotation.ParamDesc;
import com.smart.api.annotation.RequestLog;
import com.smart.api.annotation.ResponseLog;
import com.smart.api.intercepter.auth.BaseInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author chenjunlong
 * @desc 在线数据
 */
@Slf4j
@RestController
@RequestMapping("/data")
public class DataController {


    /**
     * curl 'http://localhost:8000/v1/smart-im/data/summary'
     */
    @RequestLog
    @ResponseLog
    @BaseInfo(desc = "在线数据", needAuth = true, rateLimit = 100, fallbackMethod = "summaryFallback")
    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> summary(@RequestParam(value = "room_id") @ParamDesc(desc = "房间ID") String roomId) {
        return Collections.emptyMap();
    }

    public Map<String, Object> summaryFallback(String roomId) {
        Map<String, Object> defaultMap = new HashMap<>(2);
        defaultMap.put("room_id", roomId);
        defaultMap.put("online", 100);
        return defaultMap;
    }

}
