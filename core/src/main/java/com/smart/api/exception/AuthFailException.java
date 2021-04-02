package com.smart.api.exception;


/**
 * @author chenjunlong
 */
public class AuthFailException extends ApiException {

    public AuthFailException(ExcepFactor excepFactor) {
        super(excepFactor);
    }

    public AuthFailException(ExcepFactor excepFactor, Object... args) {
        super(excepFactor, args);
    }
}
