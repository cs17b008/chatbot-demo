package com.example.n8nintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for chat responses to the frontend
 */
public class ChatResponse {

    private boolean success;
    private String message;
    private String response;
    
    @JsonProperty("conversationId")
    private String conversationId;
    
    private String timestamp;
    private Object data;

    // Default constructor
    public ChatResponse() {
        this.timestamp = LocalDateTime.now().toString();
    }

    // Constructor for successful responses
    public ChatResponse(boolean success, String message, String response, String conversationId) {
        this.success = success;
        this.message = message;
        this.response = response;
        this.conversationId = conversationId;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Constructor for error responses
    public ChatResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    // Static factory methods for common responses
    public static ChatResponse success(String response, String conversationId) {
        return new ChatResponse(true, "Chat message processed successfully", response, conversationId);
    }

    public static ChatResponse error(String errorMessage) {
        return new ChatResponse(false, errorMessage);
    }

    public static ChatResponse newConversation(String conversationId) {
        ChatResponse response = new ChatResponse(true, "New conversation started");
        response.setConversationId(conversationId);
        response.setData(new ConversationData(conversationId));
        return response;
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", response='" + response + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    // Inner class for conversation data
    public static class ConversationData {
        @JsonProperty("conversationId")
        private String conversationId;

        public ConversationData(String conversationId) {
            this.conversationId = conversationId;
        }

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }
    }
}


