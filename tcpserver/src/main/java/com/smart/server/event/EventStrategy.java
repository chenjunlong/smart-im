package com.smart.server.event;

import java.util.Map;

import javax.annotation.Resource;

import com.smart.server.event.base.Event;
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
