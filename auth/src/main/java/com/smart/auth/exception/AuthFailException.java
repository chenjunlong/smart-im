package com.smart.auth.exception;

/**
 * @author chenjunlong
 */
public class AuthFailException extends ApiException {

    public AuthFailException(ExcepFactor excepFactor) {
        super(excepFactor);
    }
}
