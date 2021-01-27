package com.smart.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author chenjunlong
 */
@AllArgsConstructor
@Getter
public class ExcepFactor {

    private int errorCode;
    private String error;

    public static final ExcepFactor E_DEFAULT = new ExcepFactor(10001, "系统错误");
    public static final ExcepFactor E_AUTH_FAILURE = new ExcepFactor(10002, "身份验证失败");
}
