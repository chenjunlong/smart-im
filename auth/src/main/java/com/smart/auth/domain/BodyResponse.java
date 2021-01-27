package com.smart.auth.domain;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author chenjunlong
 */
@Setter
@Getter
public class BodyResponse {

    @SerializedName("error_code")
    private int errorCode;
    private String error;
    private String request;
    private String detail;

    public BodyResponse(int errorCode, String error, String request) {
        this.errorCode = errorCode;
        this.error = error;
        this.request = request;
    }

    public BodyResponse(int errorCode, String error, String request, String detail) {
        this.errorCode = errorCode;
        this.error = error;
        this.request = request;
        this.detail = detail;
    }

    public static BodyResponse build(int errorCode, String error, String request) {
        return new BodyResponse(errorCode, error, request);
    }

    public static BodyResponse build(int errorCode, String error, String request, String detail) {
        return new BodyResponse(errorCode, error, request, detail);
    }

    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }
}
