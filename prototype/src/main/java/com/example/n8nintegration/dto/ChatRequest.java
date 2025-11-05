package com.example.n8nintegration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for chat requests from the frontend
 */
public class ChatRequest {

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 4000, message = "Message cannot exceed 4000 characters")
    private String message;

    @JsonProperty("conversationId")
    private String conversationId;

    @JsonProperty("userId")
    private String userId;

    // Default constructor
    public ChatRequest() {}

    // Constructor with all fields
    public ChatRequest(String message, String conversationId, String userId) {
        this.message = message;
        this.conversationId = conversationId;
        this.userId = userId;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "message='" + message + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}


