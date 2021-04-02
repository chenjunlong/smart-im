package com.smart.api.intercepter.request.validator.range;

/**
 * @author chenjunlong
 */
public interface ParamRange<T> {

    boolean isInRange(T value);

    String getDesc();

    boolean isCompatible(Class<?> type);

}
