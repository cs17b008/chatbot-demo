package com.example.n8nintegration.service;

import com.example.n8nintegration.dto.ChatRequest;
import com.example.n8nintegration.dto.ChatResponse;
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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${n8n.chat.webhook.url:${n8n.webhook.url}}")
    private String n8nChatWebhookUrl;

    @Value("${n8n.api.key:}")
    private String configuredApiKey;

    @Value("${chat.session.timeout.minutes:60}")
    private int sessionTimeoutMinutes;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Simple in-memory storage for conversation sessions
    // In production, consider using Redis or a database
    private final Map<String, ConversationSession> conversationSessions = new ConcurrentHashMap<>();

    /**
     * Start a new conversation session
     */
    public String startNewConversation(String userId) {
        String conversationId = "conv-" + UUID.randomUUID().toString();
        
        ConversationSession session = new ConversationSession(conversationId, userId);
        conversationSessions.put(conversationId, session);
        
        logger.info("Started new conversation - ConversationID: {}, UserID: {}", conversationId, userId);
        
        // Clean up old sessions periodically
        cleanupExpiredSessions();
        
        return conversationId;
    }

    /**
     * Send a chat message to the AI via N8n
     */
    public ChatResponse sendChatMessage(ChatRequest request, String requestId) {
        logger.info("Processing chat message - RequestID: {}, ConversationID: {}", 
                   requestId, request.getConversationId());

        try {
            // Get or create conversation session
            ConversationSession session = getOrCreateSession(request.getConversationId(), request.getUserId());
            
            // Prepare headers for N8n request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Request-ID", requestId);
            headers.set("X-Request-Type", "chat");
            headers.set("User-Agent", "Spring-Boot-Chat-Integration/1.0");

            // Prepare payload for N8n
            Map<String, Object> payload = createChatPayload(request, session, requestId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            // Make the request to N8n
            long startTime = System.currentTimeMillis();
            ResponseEntity<Object> response = restTemplate.exchange(
                n8nChatWebhookUrl,
                HttpMethod.POST,
                entity,
                Object.class
            );
            long duration = System.currentTimeMillis() - startTime;

            logger.info("N8N chat response - RequestID: {}, Status: {}, Duration: {}ms", 
                       requestId, response.getStatusCode(), duration);

            // Process the response from N8n
            String aiResponse = extractAiResponseFromN8n(response.getBody());
            
            // Update conversation session
            session.addMessage("user", request.getMessage());
            session.addMessage("assistant", aiResponse);
            session.updateLastActivity();

            // Return successful response
            return ChatResponse.success(aiResponse, session.getConversationId());

        } catch (RestClientException e) {
            logger.error("Failed to call N8n chat webhook - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            return ChatResponse.error("Failed to process chat message: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error processing chat message - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            return ChatResponse.error("An unexpected error occurred while processing your message");
        }
    }

    /**
     * Get conversation history
     */
    public Map<String, Object> getConversationHistory(String conversationId) {
        ConversationSession session = conversationSessions.get(conversationId);
        
        if (session == null) {
            logger.warn("Conversation not found: {}", conversationId);
            return Map.of(
                "success", false,
                "message", "Conversation not found",
                "conversationId", conversationId
            );
        }

        return Map.of(
            "success", true,
            "message", "Conversation history retrieved",
            "conversationId", conversationId,
            "messages", session.getMessages(),
            "messageCount", session.getMessages().size(),
            "createdAt", session.getCreatedAt(),
            "lastActivity", session.getLastActivity()
        );
    }

    /**
     * Test chat connection to N8n
     */
    public boolean testChatConnection() {
        try {
            logger.debug("Testing chat connection to N8n webhook: {}", n8nChatWebhookUrl);
            
            Map<String, Object> testPayload = Map.of(
                "test", true,
                "type", "chat-connection-test",
                "message", "Connection test from Spring Boot Chat Service",
                "timestamp", LocalDateTime.now().toString()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Request-Type", "test");
            headers.set("User-Agent", "Spring-Boot-Chat-Integration/1.0");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(testPayload, headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                n8nChatWebhookUrl,
                HttpMethod.POST,
                entity,
                Object.class
            );

            boolean isSuccessful = response.getStatusCode().is2xxSuccessful();
            logger.debug("Chat connection test result: {}, Status: {}", isSuccessful, response.getStatusCode());
            
            return isSuccessful;

        } catch (Exception e) {
            logger.error("Chat connection test failed: {}", e.getMessage());
            return false;
        }
    }

    // Private helper methods

    private ConversationSession getOrCreateSession(String conversationId, String userId) {
        if (conversationId != null && conversationSessions.containsKey(conversationId)) {
            return conversationSessions.get(conversationId);
        }
        
        // Create new session if not found
        String newConversationId = startNewConversation(userId);
        return conversationSessions.get(newConversationId);
    }

    private Map<String, Object> createChatPayload(ChatRequest request, ConversationSession session, String requestId) {
        Map<String, Object> payload = new HashMap<>();
        
        // Main chat data
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("message", request.getMessage());
        chatData.put("conversationId", session.getConversationId());
        chatData.put("userId", request.getUserId());
        chatData.put("messageHistory", session.getRecentMessages(10)); // Last 10 messages for context
        
        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("requestId", requestId);
        metadata.put("timestamp", LocalDateTime.now().toString());
        metadata.put("source", "spring-boot-chat");
        metadata.put("messageCount", session.getMessages().size());
        metadata.put("sessionAge", session.getSessionAgeMinutes());
        
        payload.put("chat", chatData);
        payload.put("metadata", metadata);
        payload.put("type", "chat");
        
        return payload;
    }

    private String extractAiResponseFromN8n(Object responseBody) {
        try {
            if (responseBody == null) {
                return "I apologize, but I didn't receive a proper response. Please try again.";
            }

            // Convert response to map for easier processing
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.convertValue(responseBody, Map.class);
            
            // Try different possible response fields from N8n
            if (responseMap.containsKey("response")) {
                return responseMap.get("response").toString();
            } else if (responseMap.containsKey("message")) {
                return responseMap.get("message").toString();
            } else if (responseMap.containsKey("text")) {
                return responseMap.get("text").toString();
            } else if (responseMap.containsKey("output")) {
                return responseMap.get("output").toString();
            }
            
            // If no standard field found, return the whole response as string
            return responseBody.toString();
            
        } catch (Exception e) {
            logger.warn("Failed to extract AI response from N8n response: {}", e.getMessage());
            return "I received a response but couldn't process it properly. Please try again.";
        }
    }

    private void cleanupExpiredSessions() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(sessionTimeoutMinutes);
            
            conversationSessions.entrySet().removeIf(entry -> {
                ConversationSession session = entry.getValue();
                boolean isExpired = session.getLastActivity().isBefore(cutoffTime);
                
                if (isExpired) {
                    logger.debug("Removing expired conversation session: {}", entry.getKey());
                }
                
                return isExpired;
            });
            
        } catch (Exception e) {
            logger.warn("Error during session cleanup: {}", e.getMessage());
        }
    }

    public String getChatWebhookUrl() {
        return n8nChatWebhookUrl;
    }

    // Inner class for conversation session management
    private static class ConversationSession {
        private final String conversationId;
        private final String userId;
        private final LocalDateTime createdAt;
        private LocalDateTime lastActivity;
        private final Map<String, Object> messages;

        public ConversationSession(String conversationId, String userId) {
            this.conversationId = conversationId;
            this.userId = userId;
            this.createdAt = LocalDateTime.now();
            this.lastActivity = LocalDateTime.now();
            this.messages = new HashMap<>();
            this.messages.put("history", new java.util.ArrayList<>());
        }

        public void addMessage(String role, String content) {
            Map<String, Object> message = new HashMap<>();
            message.put("role", role);
            message.put("content", content);
            message.put("timestamp", LocalDateTime.now().toString());
            
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, Object>> history = 
                (java.util.List<Map<String, Object>>) messages.get("history");
            history.add(message);
        }

        @SuppressWarnings("unchecked")
        public java.util.List<Map<String, Object>> getRecentMessages(int limit) {
            java.util.List<Map<String, Object>> history = 
                (java.util.List<Map<String, Object>>) messages.get("history");
            
            int size = history.size();
            int fromIndex = Math.max(0, size - limit);
            
            return history.subList(fromIndex, size);
        }

        public void updateLastActivity() {
            this.lastActivity = LocalDateTime.now();
        }

        public long getSessionAgeMinutes() {
            return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
        }

        // Getters
        public String getConversationId() { return conversationId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getLastActivity() { return lastActivity; }
        public Map<String, Object> getMessages() { return messages; }
    }
}
