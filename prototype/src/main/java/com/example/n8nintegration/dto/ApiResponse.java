package com.example.n8nintegration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;
    private String requestId;
    private LocalDateTime timestamp;

    // Default constructor
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with basic fields
    public ApiResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    // Constructor with all fields
    public ApiResponse(boolean success, String message, Object data, String requestId) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
        this.requestId = requestId;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
