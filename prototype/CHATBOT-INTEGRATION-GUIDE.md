# AI Chatbot Integration Guide

## Overview

This guide explains how to set up and use the AI chatbot functionality that has been integrated into your Spring Boot N8n integration platform. The chatbot provides conversational AI capabilities through your existing N8n workflows.

## 🏗️ Architecture

```
React Frontend (ChatBot Component)
    ↓ HTTP API Calls
Spring Boot Backend (ChatController + ChatService)
    ↓ Webhook Triggers
N8n Workflow (AI Processing)
    ↓ AI API Calls
AI Provider (OpenAI/Anthropic/Local LLM)
```

## 📁 New Files Added

### Backend Components

1. **DTOs**
   - `src/main/java/com/example/n8nintegration/dto/ChatRequest.java`
   - `src/main/java/com/example/n8nintegration/dto/ChatResponse.java`

2. **Services**
   - `src/main/java/com/example/n8nintegration/service/ChatService.java`

3. **Controllers**
   - `src/main/java/com/example/n8nintegration/controller/ChatController.java`

4. **Configuration**
   - Updated `src/main/resources/application.properties`
   - Updated `application-production.properties`

5. **N8n Workflow**
   - `n8n-chat-workflow.json` (sample workflow for AI processing)

## 🔌 API Endpoints

The following endpoints have been added to your Spring Boot application:

### Chat Endpoints (`/api/n8n/chat`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/n8n/chat` | Send a message to the AI chatbot |
| `POST` | `/api/n8n/chat/new` | Start a new conversation |
| `GET` | `/api/n8n/chat/history/{id}` | Get conversation history |
| `GET` | `/api/n8n/chat/test` | Test chat service connection |
| `GET` | `/api/n8n/chat/health` | Check chat service health |

### Request/Response Examples

**Send Chat Message:**
```bash
curl -X POST http://localhost:8080/api/n8n/chat \
  -H "Content-Type: application/json" \
  -H "X-API-Key: your-api-key" \
  -d '{
    "message": "Hello, how can you help me?",
    "conversationId": "conv-12345",
    "userId": "user-789"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Chat message processed successfully",
  "response": "Hello! I can help you with automation, data processing, and general questions about your n8n workflows.",
  "conversationId": "conv-12345",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## ⚙️ Configuration

### Application Properties

Add these configurations to your `application.properties`:

```properties
# N8N Chat Integration Configuration
n8n.chat.webhook.url=http://localhost:5678/webhook/chat-ai
chat.session.timeout.minutes=60
chat.max.context.messages=20
```

### Production Configuration

For production, update `application-production.properties`:

```properties
# Production Chat Configuration
n8n.chat.webhook.url=https://your-n8n-instance.com/webhook/chat-ai
chat.session.timeout.minutes=30
chat.max.context.messages=15
```

## 🤖 N8n Workflow Setup

### 1. Import the Workflow

Import the provided `n8n-chat-workflow.json` into your n8n instance:

1. Open n8n workflow editor
2. Click "Import from File"
3. Select `n8n-chat-workflow.json`
4. Configure your AI provider credentials

### 2. Workflow Components

The sample workflow includes:

- **Webhook Trigger**: Receives chat requests from Spring Boot
- **Context Processor**: Prepares conversation context for AI
- **AI Provider**: Processes messages (OpenAI example included)
- **Response Formatter**: Formats AI response for Spring Boot
- **Error Handler**: Manages errors gracefully

### 3. AI Provider Configuration

#### For OpenAI:
1. Add OpenAI credentials in n8n
2. Configure the OpenAI Chat node with:
   - Model: `gpt-3.5-turbo` or `gpt-4`
   - Temperature: `0.7`
   - Max Tokens: `500`

#### For Other Providers:
- Replace the OpenAI node with your preferred AI provider
- Ensure the response format matches what the Spring Boot service expects

## 🚀 Getting Started

### 1. Start the Services

```bash
# Start n8n (if not already running)
npx n8n start

# Start Spring Boot application
./mvnw spring-boot:run

# Start React frontend (in frontend directory)
npm start
```

### 2. Import N8n Workflow

1. Open n8n: http://localhost:5678
2. Import the chat workflow from `n8n-chat-workflow.json`
3. Configure your AI provider credentials
4. Activate the workflow

### 3. Test the Integration

```bash
# Test chat service health
curl http://localhost:8080/api/n8n/chat/health

# Test n8n connection
curl http://localhost:8080/api/n8n/chat/test

# Start a new conversation
curl -X POST http://localhost:8080/api/n8n/chat/new

# Send a test message
curl -X POST http://localhost:8080/api/n8n/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hello!",
    "conversationId": "conv-test-123"
  }'
```

## 💡 Key Features

### Session Management
- Automatic conversation session creation
- In-memory session storage (production: use Redis/Database)
- Configurable session timeouts
- Message history context for AI

### Error Handling
- Graceful fallbacks for AI service failures
- Comprehensive error logging
- User-friendly error messages
- Retry mechanisms

### Security
- API key validation (reuses existing N8n service validation)
- CORS configuration for frontend integration
- Input validation and sanitization
- Request ID tracking for debugging

### Performance
- Efficient conversation context management
- Configurable message history limits
- Automatic session cleanup
- Connection pooling and timeouts

## 🔧 Customization Options

### AI Provider Integration

You can easily switch between different AI providers by modifying the n8n workflow:

- **OpenAI**: GPT-3.5/GPT-4 models
- **Anthropic**: Claude models  
- **Azure OpenAI**: Enterprise OpenAI service
- **Local LLM**: Ollama, Hugging Face Transformers
- **Custom API**: Any REST-based AI service

### Conversation Persistence

For production environments, consider implementing persistent storage:

```java
// Example: Redis-based session storage
@Service
public class RedisChatSessionService {
    // Implement conversation persistence
}
```

### Custom Response Processing

Customize AI response handling by modifying the N8n workflow's "Format Response" node or extending the ChatService class.

## 🐛 Troubleshooting

### Common Issues

1. **Chatbot not responding**
   - Check n8n workflow is active
   - Verify webhook URL configuration
   - Test n8n connection: `GET /api/n8n/chat/test`

2. **AI provider errors**
   - Verify API credentials in n8n
   - Check rate limits and quotas
   - Review n8n workflow execution logs

3. **Session not found**
   - Sessions expire after configured timeout
   - Start new conversation: `POST /api/n8n/chat/new`

4. **CORS errors**
   - Verify `cors.allowed.origins` in properties
   - Check frontend URL configuration

### Debug Mode

Enable debug logging by adding to `application.properties`:

```properties
logging.level.com.example.n8nintegration.service.ChatService=DEBUG
logging.level.com.example.n8nintegration.controller.ChatController=DEBUG
```

## 📚 Next Steps

1. **Production Deployment**
   - Set up persistent session storage
   - Configure proper AI provider credentials
   - Implement rate limiting and monitoring

2. **Enhanced Features**
   - File upload support for documents
   - Voice message integration
   - Multi-language support
   - Custom AI personas

3. **Integration Extensions**
   - Connect to your existing databases
   - Integrate with third-party services
   - Add custom business logic workflows

## 🤝 Support

If you encounter any issues:

1. Check the application logs
2. Test individual components (Spring Boot, N8n, AI provider)
3. Verify all configuration settings
4. Review the n8n workflow execution history

The chatbot is now fully integrated with your existing N8n infrastructure and ready to provide AI-powered assistance to your users!


