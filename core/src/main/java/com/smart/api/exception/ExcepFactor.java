package com.smart.api.exception;

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
    public static final ExcepFactor E_PARAMS_ERROR = new ExcepFactor(10003, "错误:缺失必选参数: %s");
    public static final ExcepFactor E_PARAM_INVALID_ERROR = new ExcepFactor(10004, "请求参数错误 (参数: %s), 预期: %s, 实际: %s)");
}
