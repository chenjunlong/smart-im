package com.smart.api.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.smart.api.request.RequestThreadLocal;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * @author chenjunlong
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    @JsonProperty("error_code")
    private int errorCode;
    private String error;
    private String request;
    private Object body;
    private LinkedHashMap<String, Object> attributes;

    public ApiResponse(int errorCode, String error, String request) {
        this.errorCode = errorCode;
        this.error = error;
        this.request = request;
    }

    public ApiResponse(int errorCode, String error, String request, LinkedHashMap<String, Object> attributes) {
        this.errorCode = errorCode;
        this.error = error;
        this.request = request;
        this.attributes = attributes;
    }

    public static ApiResponse failure(int errorCode, String error, String request) {
        return new ApiResponse(errorCode, error, request);
    }

    public static ApiResponse failure(int errorCode, String error, String request, LinkedHashMap<String, Object> attributes) {
        return new ApiResponse(errorCode, error, request, attributes);
    }

    public static ApiResponse success(Object body) {
        return new ApiResponse(0, "success", RequestThreadLocal.getRequestPath(), body, null);
    }

    public static ApiResponse success(String msg, Object body) {
        return new ApiResponse(0, msg, RequestThreadLocal.getRequestPath(), body, null);
    }

}
