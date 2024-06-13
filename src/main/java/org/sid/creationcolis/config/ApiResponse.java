package org.sid.creationcolis.config;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class ApiResponse {
    private boolean success;
    private Map<String, Object> data;
    private String message;



    public ApiResponse(boolean success, Map<String, Object> data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

