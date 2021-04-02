package com.smart.api.intercepter.request.validator;

/**
 * @author chenjunlong
 */
public interface TypeValidator {

    boolean isValid(String[] values);

    String getDesc();

}
