package com.smart.api.exception;

import java.util.LinkedHashMap;

/**
 * @author chenjunlong
 */
public class ApiException extends RuntimeException {

    private int errorCode;
    private String error;
    private LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
    private Object body;

    public ApiException(ExcepFactor excepFactor) {
        this.errorCode = excepFactor.getErrorCode();
        this.error = excepFactor.getError();
    }

    public ApiException(ExcepFactor excepFactor, Object... args) {
        this.errorCode = excepFactor.getErrorCode();
        this.error = (args == null || args.length == 0) ? excepFactor.getError() : String.format(excepFactor.getError(), args);
    }

    public ApiException(ExcepFactor excepFactor, Object body) {
        this.errorCode = excepFactor.getErrorCode();
        this.error = excepFactor.getError();
        this.body = body;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

    public Object getBody() {
        return body;
    }

    public ApiException setAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    public LinkedHashMap<String, Object> getAttributes() {
        return attributes;
    }
}
