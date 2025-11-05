package com.example.n8nintegration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private String message;
    private Object data;

    // Default constructor
    public WebhookRequest() {}

    // Constructor with required fields
    public WebhookRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Constructor with all fields
    public WebhookRequest(String name, String email, String message, Object data) {
        this.name = name;
        this.email = email;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "WebhookRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
