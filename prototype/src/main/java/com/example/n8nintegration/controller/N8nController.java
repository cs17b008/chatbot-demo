package com.example.n8nintegration.controller;

import com.example.n8nintegration.service.N8nService;
import com.example.n8nintegration.service.ChatService;
import com.example.n8nintegration.dto.WebhookRequest;
import com.example.n8nintegration.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/n8n")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class N8nController {

    private static final Logger logger = LoggerFactory.getLogger(N8nController.class);

    @Autowired
    private N8nService n8nService;

    @Autowired
    private ChatService chatService;

    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse> triggerWebhook(
            @Valid @RequestBody WebhookRequest request,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        String requestId = UUID.randomUUID().toString();
        
        logger.info("Received webhook trigger request - RequestID: {}, Payload: {}", 
                   requestId, request);

        try {
            // Validate API key if configured
            if (!n8nService.isValidApiKey(apiKey)) {
                logger.warn("Invalid or missing API key - RequestID: {}", requestId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid or missing API key", null, requestId));
            }

            // Forward request to n8n webhook
            Object response = n8nService.triggerWebhook(request, requestId);
            
            logger.info("Successfully triggered n8n webhook - RequestID: {}, Response: {}", 
                       requestId, response);

            return ResponseEntity.ok(
                new ApiResponse(true, "Webhook triggered successfully", response, requestId));

        } catch (Exception e) {
            logger.error("Error triggering n8n webhook - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Failed to trigger webhook: " + e.getMessage(), null, requestId));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        logger.debug("Health check requested");
        
        try {
            // Basic health data
            java.util.Map<String, Object> healthData = new java.util.HashMap<>();
            healthData.put("service", "N8N Integration Platform");
            healthData.put("status", "running");
            healthData.put("timestamp", java.time.LocalDateTime.now().toString());
            healthData.put("webhookUrl", n8nService.getWebhookUrl());
            healthData.put("chatWebhookUrl", chatService.getChatWebhookUrl());
            healthData.put("features", java.util.List.of("webhook-triggers", "ai-chat", "data-processing"));
            
            return ResponseEntity.ok(
                new ApiResponse(true, "N8N integration service is running with chat functionality", healthData, null));
                
        } catch (Exception e) {
            logger.warn("Health check encountered minor issues: {}", e.getMessage());
            return ResponseEntity.ok(
                new ApiResponse(true, "N8N integration service is running (partial functionality)", null, null));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse> testEndpoint() {
        String requestId = UUID.randomUUID().toString();
        logger.info("Test endpoint called - RequestID: {}", requestId);
        
        try {
            boolean isConnected = n8nService.testConnection();
            String message = isConnected ? "N8N connection test successful" : "N8N connection test failed";
            
            return ResponseEntity.ok(
                new ApiResponse(isConnected, message, null, requestId));
                
        } catch (Exception e) {
            logger.error("Error testing n8n connection - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Connection test failed: " + e.getMessage(), null, requestId));
        }
    }
}
