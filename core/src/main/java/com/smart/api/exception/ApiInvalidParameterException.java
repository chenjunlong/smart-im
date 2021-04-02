package com.smart.api.exception;

/**
 * @author chenjunlong
 */
public class ApiInvalidParameterException extends ApiException {

    public ApiInvalidParameterException(ExcepFactor excepFactor) {
        super(excepFactor);
    }

    public ApiInvalidParameterException(ExcepFactor excepFactor, Object... args) {
        super(excepFactor, args);
    }
}
