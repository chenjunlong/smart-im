package com.smart.api.intercepter.ratelimit;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenjunlong
 */
public class QpsStorage {

    private static Map<String, QpsCounter> storage = new HashMap<>();

    public static QpsCounter create(String counterName) {
        return storage.putIfAbsent(counterName, new QpsCounter());
    }

    public static QpsCounter get(String counterName) {
        return storage.get(counterName);
    }
}
