package com.smart.server.model;

import com.google.gson.Gson;
import lombok.Data;

/**
 * @author chenjunlong
 */
@Data
public class ConnectRequest {

    private String roomId;
    private long uid;
    private String msg;

    private static final Gson gson = new Gson();

    public String toJson() {
        return gson.toJson(this);
    }

    public static ConnectRequest parseFromJson(String json) {
        return gson.fromJson(json, ConnectRequest.class);
    }

}
