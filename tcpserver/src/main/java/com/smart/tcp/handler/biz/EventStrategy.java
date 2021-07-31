package com.smart.tcp.handler.biz;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

/**
 * @author chenjunlong
 */
@Component
public class EventStrategy {

    @Resource
    private Map<String, Event> strategyMap;

    public Event build(String eventName) {
        return strategyMap.get(eventName);
    }

}
