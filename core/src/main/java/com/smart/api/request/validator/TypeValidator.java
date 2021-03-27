package com.smart.api.request.validator;

/**
 * @author chenjunlong
 */
public interface TypeValidator {

    boolean isValid(String[] values);

    String getDesc();

}
