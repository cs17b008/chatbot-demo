package com.example.n8nintegration.service;

import com.example.n8nintegration.dto.WebhookRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class N8nService {

    private static final Logger logger = LoggerFactory.getLogger(N8nService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${n8n.webhook.url}")
    private String n8nWebhookUrl;

    @Value("${n8n.api.key:}")
    private String configuredApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Object triggerWebhook(WebhookRequest request, String requestId) {
        logger.info("Triggering n8n webhook - RequestID: {}, URL: {}", requestId, n8nWebhookUrl);

        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Request-ID", requestId);
            headers.set("User-Agent", "Spring-Boot-N8N-Integration/1.0");

            // Prepare payload with metadata
            Map<String, Object> payload = new HashMap<>();
            payload.put("data", request);
            payload.put("metadata", Map.of(
                "requestId", requestId,
                "timestamp", LocalDateTime.now().toString(),
                "source", "spring-boot-middleware"
            ));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            // Make the request to n8n
            long startTime = System.currentTimeMillis();
            ResponseEntity<Object> response = restTemplate.exchange(
                n8nWebhookUrl,
                HttpMethod.POST,
                entity,
                Object.class
            );
            long duration = System.currentTimeMillis() - startTime;

            logger.info("N8N webhook response - RequestID: {}, Status: {}, Duration: {}ms", 
                       requestId, response.getStatusCode(), duration);

            return response.getBody();

        } catch (RestClientException e) {
            logger.error("Failed to call n8n webhook - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            throw new RuntimeException("Failed to trigger n8n webhook: " + e.getMessage(), e);
        }
    }

    public boolean testConnection() {
        try {
            logger.debug("Testing connection to n8n webhook: {}", n8nWebhookUrl);
            
            // Simple test payload
            Map<String, Object> testPayload = Map.of(
                "test", true,
                "message", "Connection test from Spring Boot",
                "timestamp", LocalDateTime.now().toString()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Spring-Boot-N8N-Integration/1.0");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(testPayload, headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                n8nWebhookUrl,
                HttpMethod.POST,
                entity,
                Object.class
            );

            boolean isSuccessful = response.getStatusCode().is2xxSuccessful();
            logger.debug("Connection test result: {}, Status: {}", isSuccessful, response.getStatusCode());
            
            return isSuccessful;

        } catch (Exception e) {
            logger.error("Connection test failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isValidApiKey(String providedApiKey) {
        // If no API key is configured, skip validation
        if (configuredApiKey == null || configuredApiKey.trim().isEmpty()) {
            logger.debug("No API key configured, skipping validation");
            return true;
        }

        // If API key is configured, validate it
        boolean isValid = configuredApiKey.equals(providedApiKey);
        logger.debug("API key validation result: {}", isValid);
        
        return isValid;
    }

    public String getWebhookUrl() {
        return n8nWebhookUrl;
    }
}
