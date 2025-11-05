# N8N Webhook Integration Testing Guide

## Prerequisites

1. **n8n installed and running** on `http://localhost:5678`
2. **Spring Boot application running** on `http://localhost:8080`
3. **Webhook URL configured** in `application.properties`

## Quick Test Sequence

### 1. Verify Services Are Running

```bash
# Test Spring Boot health
curl http://localhost:8080/api/n8n/health

# Expected response:
{
  "success": true,
  "message": "N8N integration service is running",
  "data": null,
  "requestId": null,
  "timestamp": "2023-12-07T10:30:00"
}
```

### 2. Test n8n Connection

```bash
curl http://localhost:8080/api/n8n/test

# Expected response (if n8n is reachable):
{
  "success": true,
  "message": "N8N connection test successful",
  "data": null,
  "requestId": "uuid-here",
  "timestamp": "2023-12-07T10:30:00"
}
```

### 3. Test Basic Webhook Trigger

```bash
curl -X POST http://localhost:8080/api/n8n/trigger \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "message": "Hello from Spring Boot!",
    "data": {
      "testField": "testValue"
    }
  }'
```

### 4. Test with Invalid Data (Validation)

```bash
curl -X POST http://localhost:8080/api/n8n/trigger \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "email": "invalid-email",
    "message": "This should fail"
  }'

# Expected: 400 Bad Request with validation errors
```

### 5. Test with API Key (if configured)

```bash
curl -X POST http://localhost:8080/api/n8n/trigger \
  -H "Content-Type: application/json" \
  -H "X-API-Key: your-api-key" \
  -d '{
    "name": "Authenticated User",
    "email": "auth@example.com",
    "message": "Authenticated request"
  }'
```

## N8N Workflow Setup

### Basic Workflow Structure

1. **Webhook Node** (Trigger)
   - Method: POST
   - Path: `/your-webhook-path`
   - Authentication: None (for testing)

2. **Set Node** (Data Processing)
   ```json
   {
     "processedData": {
       "originalName": "={{ $json.data.name }}",
       "originalEmail": "={{ $json.data.email }}",
       "processedAt": "={{ $now }}",
       "requestId": "={{ $json.metadata.requestId }}",
       "status": "processed"
     }
   }
   ```

3. **Respond to Webhook Node**
   - Response: JSON
   - Body: 
   ```json
   {
     "success": true,
     "message": "Processed by n8n",
     "data": "={{ $json }}"
   }
   ```

### Import Sample Workflow

1. Copy the content from `n8n-sample-workflow.json`
2. In n8n interface, go to Templates → Import from JSON
3. Paste the workflow JSON
4. Save and activate the workflow
5. Copy the webhook URL and update your `application.properties`

## Troubleshooting

### Common Issues

1. **Connection Refused**
   ```
   Error: Connection refused to http://localhost:5678/webhook/...
   ```
   - **Solution**: Ensure n8n is running: `n8n start`

2. **Webhook Not Found (404)**
   ```
   Error: 404 Not Found
   ```
   - **Solution**: Check webhook URL in application.properties
   - **Solution**: Ensure n8n workflow is activated

3. **CORS Errors (in browser)**
   ```
   Error: Access to fetch blocked by CORS policy
   ```
   - **Solution**: Update `cors.allowed.origins` in application.properties

4. **API Key Validation Failed**
   ```
   Error: Invalid or missing API key
   ```
   - **Solution**: Either provide correct X-API-Key header or remove n8n.api.key from properties

5. **Validation Errors**
   ```
   Error: Validation failed for field 'email'
   ```
   - **Solution**: Ensure request body matches WebhookRequest DTO requirements

### Debugging Tips

1. **Enable Debug Logging**
   ```properties
   logging.level.com.example.n8nintegration=DEBUG
   logging.level.org.springframework.web.client.RestTemplate=DEBUG
   ```

2. **Check n8n Execution Logs**
   - In n8n interface, go to Executions tab
   - Check failed/successful executions
   - Review input/output data

3. **Monitor Spring Boot Logs**
   ```bash
   # Watch logs in real-time
   tail -f logs/spring.log
   ```

## Performance Testing

### Load Testing with curl

```bash
# Simple load test (10 requests)
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/n8n/trigger \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"User$i\",\"email\":\"user$i@test.com\",\"message\":\"Load test $i\"}" &
done
wait
```

### Expected Response Times

- **Health Check**: < 50ms
- **Connection Test**: < 200ms
- **Webhook Trigger**: < 500ms (depends on n8n workflow complexity)

## Integration with Frontend

### JavaScript/React Example

```javascript
const triggerWebhook = async (data) => {
  try {
    const response = await fetch('http://localhost:8080/api/n8n/trigger', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-API-Key': 'your-api-key' // if required
      },
      body: JSON.stringify(data)
    });
    
    const result = await response.json();
    console.log('Webhook response:', result);
    return result;
  } catch (error) {
    console.error('Webhook error:', error);
    throw error;
  }
};

// Usage
triggerWebhook({
  name: 'John Doe',
  email: 'john@example.com',
  message: 'Hello from React!',
  data: { source: 'web-form' }
});
```

## Production Checklist

- [ ] Configure production n8n URL
- [ ] Set up API key authentication
- [ ] Configure proper CORS origins
- [ ] Set up logging to files
- [ ] Configure SSL/HTTPS
- [ ] Set up monitoring and health checks
- [ ] Test error scenarios
- [ ] Validate all webhook endpoints
- [ ] Performance test under load
- [ ] Set up backup/recovery procedures
