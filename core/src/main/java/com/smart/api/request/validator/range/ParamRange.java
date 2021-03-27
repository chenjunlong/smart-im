package com.smart.api.request.validator.range;

/**
 * @author chenjunlong
 */
public interface ParamRange<T> {

    boolean isInRange(T value);

    String getDesc();

    boolean isCompatible(Class<?> type);

}
