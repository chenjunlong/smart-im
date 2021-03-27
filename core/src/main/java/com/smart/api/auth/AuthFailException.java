package com.smart.api.auth;


import com.smart.api.exception.ApiException;
import com.smart.api.exception.ExcepFactor;

/**
 * @author chenjunlong
 */
public class AuthFailException extends ApiException {

    public AuthFailException(ExcepFactor excepFactor) {
        super(excepFactor);
    }
}
