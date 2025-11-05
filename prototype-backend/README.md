# N8N Integration Prototype

A Spring Boot application that serves as middleware between a React frontend and n8n workflows.

## Features

- RESTful API endpoints for n8n webhook integration
- Configurable webhook URLs via application.properties
- API key authentication
- Structured logging with request tracking
- CORS configuration for React frontend
- Global exception handling
- Health check and connection test endpoints

## Project Structure

```
prototype/
├── src/main/java/com/example/n8nintegration/
│   ├── N8nIntegrationApplication.java          # Main Spring Boot application
│   ├── config/
│   │   └── CorsConfig.java                     # CORS configuration
│   ├── controller/
│   │   └── N8nController.java                  # REST API endpoints
│   ├── service/
│   │   └── N8nService.java                     # Business logic for n8n integration
│   ├── dto/
│   │   ├── WebhookRequest.java                 # Request payload structure
│   │   └── ApiResponse.java                    # Response payload structure
│   └── exception/
│       └── GlobalExceptionHandler.java         # Global exception handling
├── src/main/resources/
│   └── application.properties                  # Configuration properties
├── pom.xml                                     # Maven dependencies
└── README.md                                   # This file
```

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- n8n instance running (default: localhost:5678)

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Update with your actual n8n webhook URL
n8n.webhook.url=http://localhost:5678/webhook/your-webhook-id

# Set API key for authentication (optional)
n8n.api.key=your-api-key-here
```

## Running the Application

1. Navigate to the project directory:
   ```bash
   cd C:\Users\%USERNAME%\Desktop\prototype
   ```

2. Build the project:
   ```bash
   mvn clean compile
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### POST /api/n8n/trigger
Triggers an n8n webhook with the provided payload.

**Headers:**
- `Content-Type: application/json`
- `X-API-Key: your-api-key` (if API key authentication is enabled)

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "message": "Test message",
  "data": {
    "additional": "data"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Webhook triggered successfully",
  "data": { /* n8n response data */ },
  "requestId": "uuid-string",
  "timestamp": "2023-12-07T10:30:00"
}
```

### GET /api/n8n/health
Health check endpoint.

### GET /api/n8n/test
Tests the connection to the configured n8n webhook.

## Testing with Postman

1. **Test Connection:**
   - GET `http://localhost:8080/api/n8n/test`

2. **Trigger Webhook:**
   - POST `http://localhost:8080/api/n8n/trigger`
   - Headers: `Content-Type: application/json`
   - Body: Use the sample JSON above

## Logging

The application includes structured logging that captures:
- Request IDs for tracing
- Incoming payloads and responses
- API call durations
- Error details

Logs are output to the console and can be configured to write to files.

## Next Steps

1. Update the `n8n.webhook.url` in `application.properties` with your actual webhook URL
2. Test the endpoints using Postman
3. Set up API key authentication if needed
4. Integrate with your React frontend

## Troubleshooting

- **Connection refused:** Ensure your n8n instance is running on the configured URL
- **CORS errors:** Check that the React app origin is included in `cors.allowed.origins`
- **Authentication errors:** Verify the API key configuration matches between client and server
