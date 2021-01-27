package com.smart.auth.exception;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chenjunlong
 */
public class ApiException extends RuntimeException {

    private ExcepFactor excepFactor;
    private LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();

    public ApiException(ExcepFactor excepFactor) {
        this.excepFactor = excepFactor;
    }

    public int getErrorCode() {
        return excepFactor.getErrorCode();
    }

    public String getError() {
        return excepFactor.getError();
    }

    public ApiException setAttribute(String key, Object value) {
        attributes.put(key, value);
        return this;
    }

    public String printDetail() {
        String detail = "";
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            detail += "\n" + entry.getKey() + "=" + entry.getValue();
        }
        return detail;
    }
}
