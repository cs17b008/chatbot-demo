package com.example.n8nintegration.controller;

import com.example.n8nintegration.dto.ApiResponse;
import com.example.n8nintegration.dto.ChatRequest;
import com.example.n8nintegration.dto.ChatResponse;
import com.example.n8nintegration.service.ChatService;
import com.example.n8nintegration.service.N8nService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/n8n/chat")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private N8nService n8nService;

    /**
     * Send a chat message to the AI
     * POST /api/n8n/chat
     */
    @PostMapping
    public ResponseEntity<ChatResponse> sendMessage(
            @Valid @RequestBody ChatRequest request,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        String requestId = UUID.randomUUID().toString();
        
        logger.info("Received chat message - RequestID: {}, ConversationID: {}, Message: {}", 
                   requestId, request.getConversationId(), 
                   request.getMessage().length() > 100 ? 
                   request.getMessage().substring(0, 100) + "..." : request.getMessage());

        try {
            // Validate API key if configured (reuse existing validation from N8nService)
            if (!n8nService.isValidApiKey(apiKey)) {
                logger.warn("Invalid or missing API key for chat request - RequestID: {}", requestId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ChatResponse.error("Invalid or missing API key"));
            }

            // Process the chat message
            ChatResponse response = chatService.sendChatMessage(request, requestId);
            
            if (response.isSuccess()) {
                logger.info("Successfully processed chat message - RequestID: {}, ConversationID: {}", 
                           requestId, response.getConversationId());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Chat message processing failed - RequestID: {}, Error: {}", 
                           requestId, response.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

        } catch (Exception e) {
            logger.error("Error processing chat message - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            
            ChatResponse errorResponse = ChatResponse.error("Failed to process chat message: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Start a new conversation
     * POST /api/n8n/chat/new
     */
    @PostMapping("/new")
    public ResponseEntity<ApiResponse> startNewConversation(
            @RequestParam(value = "userId", required = false, defaultValue = "anonymous") String userId,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        String requestId = UUID.randomUUID().toString();
        logger.info("Starting new conversation - RequestID: {}, UserID: {}", requestId, userId);

        try {
            // Validate API key if configured
            if (!n8nService.isValidApiKey(apiKey)) {
                logger.warn("Invalid or missing API key for new conversation - RequestID: {}", requestId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid or missing API key", null, requestId));
            }

            // Generate conversation ID
            String conversationId = chatService.startNewConversation(userId);
            
            // Prepare response data
            Map<String, Object> responseData = Map.of(
                "conversationId", conversationId,
                "userId", userId,
                "status", "active"
            );

            logger.info("New conversation started - RequestID: {}, ConversationID: {}", 
                       requestId, conversationId);

            return ResponseEntity.ok(
                new ApiResponse(true, "New conversation started successfully", responseData, requestId));

        } catch (Exception e) {
            logger.error("Error starting new conversation - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Failed to start new conversation: " + e.getMessage(), null, requestId));
        }
    }

    /**
     * Get conversation history
     * GET /api/n8n/chat/history/{conversationId}
     */
    @GetMapping("/history/{conversationId}")
    public ResponseEntity<ApiResponse> getChatHistory(
            @PathVariable String conversationId,
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        String requestId = UUID.randomUUID().toString();
        logger.info("Retrieving chat history - RequestID: {}, ConversationID: {}", 
                   requestId, conversationId);

        try {
            // Validate API key if configured
            if (!n8nService.isValidApiKey(apiKey)) {
                logger.warn("Invalid or missing API key for chat history - RequestID: {}", requestId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid or missing API key", null, requestId));
            }

            // Get conversation history
            Map<String, Object> historyData = chatService.getConversationHistory(conversationId);
            
            boolean success = (Boolean) historyData.get("success");
            String message = (String) historyData.get("message");

            if (success) {
                logger.info("Chat history retrieved successfully - RequestID: {}, ConversationID: {}, MessageCount: {}", 
                           requestId, conversationId, historyData.get("messageCount"));
                
                return ResponseEntity.ok(
                    new ApiResponse(true, message, historyData, requestId));
            } else {
                logger.warn("Chat history not found - RequestID: {}, ConversationID: {}", 
                           requestId, conversationId);
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, message, null, requestId));
            }

        } catch (Exception e) {
            logger.error("Error retrieving chat history - RequestID: {}, ConversationID: {}, Error: {}", 
                        requestId, conversationId, e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Failed to retrieve chat history: " + e.getMessage(), null, requestId));
        }
    }

    /**
     * Test chat functionality and N8n connection
     * GET /api/n8n/chat/test
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse> testChatConnection() {
        String requestId = UUID.randomUUID().toString();
        logger.info("Chat connection test requested - RequestID: {}", requestId);
        
        try {
            boolean isConnected = chatService.testChatConnection();
            String message = isConnected ? 
                "Chat service and N8N connection test successful" : 
                "Chat service or N8N connection test failed";
            
            Map<String, Object> testData = Map.of(
                "chatServiceConnected", isConnected,
                "n8nChatWebhookUrl", chatService.getChatWebhookUrl(),
                "testTimestamp", java.time.LocalDateTime.now().toString()
            );

            HttpStatus status = isConnected ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
            
            return ResponseEntity.status(status)
                .body(new ApiResponse(isConnected, message, testData, requestId));
                
        } catch (Exception e) {
            logger.error("Error testing chat connection - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Chat connection test failed: " + e.getMessage(), null, requestId));
        }
    }

    /**
     * Get chat service health and statistics
     * GET /api/n8n/chat/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> chatHealthCheck() {
        String requestId = UUID.randomUUID().toString();
        logger.debug("Chat health check requested - RequestID: {}", requestId);
        
        try {
            Map<String, Object> healthData = Map.of(
                "status", "running",
                "service", "ChatService",
                "n8nChatWebhookUrl", chatService.getChatWebhookUrl(),
                "timestamp", java.time.LocalDateTime.now().toString()
            );

            return ResponseEntity.ok(
                new ApiResponse(true, "Chat service is running and healthy", healthData, requestId));
                
        } catch (Exception e) {
            logger.error("Error in chat health check - RequestID: {}, Error: {}", 
                        requestId, e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Chat health check failed: " + e.getMessage(), null, requestId));
        }
    }
}


