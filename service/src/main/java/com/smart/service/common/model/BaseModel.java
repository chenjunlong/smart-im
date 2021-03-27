package com.smart.service.common.model;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * @author chenjunlong
 */
public abstract class BaseModel {

    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }

    public static <T> T toObject(String value, Class clazz) {
        return (T) gson.fromJson(value, clazz);
    }
}
