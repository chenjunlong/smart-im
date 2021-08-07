package com.smart.biz.common.model;

import com.google.gson.Gson;

/**
 * @author chenjunlong
 */
public abstract class BaseModel {

    /**
     * ******************* json serializable *********************
     */
    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }

    public static <T> T parseFromJson(String value, Class clazz) {
        return (T) gson.fromJson(value, clazz);
    }

    public static String toJson(Object value) {
        return gson.toJson(value);
    }

}
