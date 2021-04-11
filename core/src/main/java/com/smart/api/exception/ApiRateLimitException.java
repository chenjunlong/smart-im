package com.smart.api.exception;

/**
 * @author chenjunlong
 */
public class ApiRateLimitException extends ApiException {

    public ApiRateLimitException(ExcepFactor excepFactor) {
        super(excepFactor);
    }

    public ApiRateLimitException(ExcepFactor excepFactor, Object... args) {
        super(excepFactor, args);
    }

    public ApiRateLimitException(ExcepFactor excepFactor, Object body) {
        super(excepFactor, body);
    }
}
