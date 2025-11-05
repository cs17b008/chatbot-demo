# 🚀 N8N Integration Prototype including chatbot development


A modern, enterprise-grade React TypeScript frontend that seamlessly integrates with Spring Boot API and n8n workflows, featuring an intelligent AI chatbot and real-time webhook processing.

[![React](https://img.shields.io/badge/React-19.1.1-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-4.9.5-blue.svg)](https://www.typescriptlang.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3.2-purple.svg)](https://getbootstrap.com/)
[![n8n](https://img.shields.io/badge/n8n-Compatible-orange.svg)](https://n8n.io/)

## 📋 Table of Contents

- [🎯 Features](#-features)
- [🏗️ System Architecture](#️-system-architecture)
- [⚡ Prerequisites](#-prerequisites)
- [🔧 Installation & Setup](#-installation--setup)
- [🔐 Credentials & Configuration](#-credentials--configuration)
- [📊 N8N Workflow Setup](#-n8n-workflow-setup)
- [🌐 Spring Boot Backend Integration](#-spring-boot-backend-integration)
- [🚀 Running the Application](#-running-the-application)
- [🔧 Development](#-development)
- [📁 Project Structure](#-project-structure)
- [🤖 AI Chatbot Implementation](#-ai-chatbot-implementation)
- [🔍 API Reference](#-api-reference)
- [🐛 Troubleshooting](#-troubleshooting)
- [🚢 Deployment](#-deployment)
- [🤝 Contributing](#-contributing)

## 🎯 Features

### ✨ Core Features
- 🚀 **Real-time Service Status** - Monitor Spring Boot and n8n connectivity with live health checks
- 📝 **Interactive Webhook Form** - Send structured data to n8n workflows with comprehensive validation
- 🤖 **AI-Powered Chatbot** - Intelligent assistant powered by n8n workflows and Spring Boot backend
- 🎨 **Modern UI/UX** - Built with Bootstrap 5, Lucide icons, and responsive design
- 🔄 **Live Response Handling** - Real-time feedback from n8n workflow executions
- 📱 **Mobile-First Design** - Fully responsive across all device sizes
- 🔐 **Secure API Integration** - Token-based authentication and CORS-enabled communication

### 🛠️ Technical Features
- **TypeScript Support** - Full type safety across the application
- **Error Boundary Handling** - Graceful error recovery and user feedback
- **Loading States** - Animated loading indicators for better UX
- **Form Validation** - Client-side and server-side validation
- **API Rate Limiting** - Built-in request throttling and retry logic
- **Markdown Support** - Rich text rendering in chat responses

## 🏗️ System Architecture

graph TB

    A["React Frontend :3000"] --> B["Spring Boot API :8080"]
    B --> C["n8n Workflows :5678"]
    C --> D["External APIs"]
    C --> E["Database"]
    C --> F["Email Services"]
    
    subgraph "Frontend Components"
        G["ChatBot"]
        H["WebhookForm"]
        I["StatusDashboard"]
    end
    
    subgraph "API Endpoints"
        J["/api/n8n/health"]
        K["/api/n8n/trigger"]
        L["/api/n8n/chat"]
        M["/api/n8n/test"]
    end
    
    A --> G
    A --> H
    A --> I
    B --> J
    B --> K
    B --> L
    B --> M


### Data Flow
1. **User Interaction** → React Frontend captures user input
2. **API Communication** → Spring Boot processes and validates requests
3. **Workflow Execution** → n8n executes automated workflows
4. **Response Handling** → Results flow back through the stack
5. **UI Updates** → Real-time updates reflect in the frontend

## ⚡ Prerequisites

### Required Software
| Software | Version | Purpose |
|----------|---------|---------|
| **Node.js** | ≥ 16.0.0 | JavaScript runtime for React development |
| **npm** | ≥ 8.0.0 | Package manager |
| **Java** | ≥ 11 | Spring Boot backend runtime |
| **n8n** | Latest | Workflow automation platform |
| **Git** | Latest | Version control |

### System Requirements
- **RAM**: Minimum 4GB, Recommended 8GB+
- **Storage**: 2GB free space
- **OS**: Windows 10+, macOS 10.15+, or Linux (Ubuntu 18.04+)
- **Browser**: Chrome 90+, Firefox 88+, Safari 14+, Edge 90+

### Network Requirements
- **Ports**: 3000 (React), 8080 (Spring Boot), 5678 (n8n)
- **Internet**: Required for package installation and external API calls

## 🔧 Installation & Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd prototype-frontend
```

### 2. Install Dependencies
```bash
# Install all npm dependencies
npm install

# Verify installation
npm list --depth=0
```

### 3. Environment Configuration
Create environment files for different stages:

#### Development Environment
```bash
# Create .env.development
cat > .env.development << EOF
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_N8N_URL=http://localhost:5678
REACT_APP_ENVIRONMENT=development
REACT_APP_DEBUG_MODE=true
EOF
```

#### Production Environment
```bash
# Create .env.production
cat > .env.production << EOF
REACT_APP_API_BASE_URL=https://your-api-domain.com
REACT_APP_N8N_URL=https://your-n8n-domain.com
REACT_APP_ENVIRONMENT=production
REACT_APP_DEBUG_MODE=false
EOF
```

## 🔐 Credentials & Configuration

### N8N Authentication Setup

#### 1. Basic Authentication (Development)
```bash
# n8n environment variables
export N8N_BASIC_AUTH_ACTIVE=true
export N8N_BASIC_AUTH_USER=admin
export N8N_BASIC_AUTH_PASSWORD=your_secure_password
```

#### 2. JWT Authentication (Production)
```bash
# Generate JWT secret
export N8N_JWT_AUTH_ACTIVE=true
export N8N_JWT_AUTH_HEADER=authorization
export N8N_JWT_AUTH_HEADER_VALUE_PREFIX="Bearer "
export JWT_AUTH_SECRET=your_jwt_secret_key_here
```

#### 3. API Key Authentication
```bash
# For webhook security
export N8N_WEBHOOK_API_KEY=your_api_key_here
export N8N_WEBHOOK_API_KEY_HEADER=X-API-Key
```

### Spring Boot Configuration

#### application.properties
```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/

# N8N Integration
n8n.base.url=http://localhost:5678
n8n.webhook.url=${n8n.base.url}/webhook
n8n.api.key=${N8N_WEBHOOK_API_KEY:your_default_api_key}

# CORS Configuration
cors.allowed.origins=http://localhost:3000,https://your-frontend-domain.com
cors.allowed.methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed.headers=*

# Database Configuration (if needed)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Logging
logging.level.com.yourpackage=DEBUG
logging.level.org.springframework.web=DEBUG
```

### Database Credentials (if using persistent storage)
```bash
# PostgreSQL example
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=n8n_integration
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password
```

## 📊 N8N Workflow Setup

### 1. Install and Start n8n

#### Option A: Using npx (Recommended for development)
```bash
# Install and start n8n
npx n8n

# Or install globally
npm install n8n -g
n8n start
```

#### Option B: Using Docker
```bash
# Create docker-compose.yml
cat > docker-compose.yml << EOF
version: '3.8'
services:
  n8n:
    image: n8nio/n8n
    ports:
      - "5678:5678"
    environment:
      - N8N_BASIC_AUTH_ACTIVE=true
      - N8N_BASIC_AUTH_USER=admin
      - N8N_BASIC_AUTH_PASSWORD=password
      - WEBHOOK_URL=http://localhost:5678/
    volumes:
      - n8n_data:/home/node/.n8n
volumes:
  n8n_data:
EOF

# Start n8n
docker-compose up -d
```

### 2. Configure n8n Environment Variables

Based on your deprecation warnings, update your n8n configuration:

```bash
# Recommended settings from your deprecation warnings
export DB_SQLITE_POOL_SIZE=5
export N8N_RUNNERS_ENABLED=true

# Additional production settings
export N8N_PROTOCOL=https
export N8N_HOST=your-n8n-domain.com
export N8N_PORT=443
export N8N_LISTEN_ADDRESS=0.0.0.0
export WEBHOOK_URL=https://your-n8n-domain.com/
```

### 3. Create Essential Workflows

#### Webhook Receiver Workflow
1. **Access n8n**: Open `http://localhost:5678`
2. **Create New Workflow**: Click "New workflow"
3. **Add Webhook Node**:
   - **HTTP Method**: POST
   - **Path**: `/webhook/data-receiver`
   - **Authentication**: None (for development)
   - **Response Mode**: Return response from last node

4. **Add Processing Nodes**: Create a workflow that processes incoming data
5. **Activate Workflow**: Click the toggle switch to activate

#### AI Chat Workflow
1. **Create Chat Workflow**:
   - **Trigger**: Webhook with path `/webhook/chat`
   - **AI Node**: OpenAI/ChatGPT node (requires API key)
   - **Response**: Format and return AI response

2. **Configure OpenAI Node**:
   ```bash
   # Add to n8n credentials
   export OPENAI_API_KEY=your_openai_api_key
   ```

### 4. Test Workflows
```bash
# Test webhook endpoint
curl -X POST http://localhost:5678/webhook/data-receiver \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "message": "Test message"
  }'
```

## 🌐 Spring Boot Backend Integration

This frontend is designed to work with a Spring Boot backend that acts as a middleware between React and n8n.

### Required Dependencies (pom.xml)
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### Sample Controller Implementation
```java
@RestController
@RequestMapping("/api/n8n")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class N8nController {
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message("Spring Boot service is healthy")
                .timestamp(LocalDateTime.now())
                .build()
        );
    }
    
    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse> triggerWebhook(
            @Valid @RequestBody WebhookRequest request) {
        // Implementation to forward request to n8n
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> sendChatMessage(
            @Valid @RequestBody ChatRequest request) {
        // Implementation for chat functionality
        return ResponseEntity.ok(response);
    }
}
```

## 🚀 Running the Application

### Development Mode

#### 1. Start n8n
```bash
# Terminal 1: Start n8n
n8n start

# Verify n8n is running
curl http://localhost:5678/healthz
```

#### 2. Start Spring Boot Backend
```bash
# Terminal 2: Start Spring Boot (if you have the backend)
cd ../spring-boot-backend
./mvnw spring-boot:run

# Or if using JAR
java -jar target/n8n-integration-backend.jar
```

#### 3. Start React Frontend
```bash
# Terminal 3: Start React development server
npm start

# Application will open at http://localhost:3000
```

### Production Mode

#### Build and Deploy
```bash
# Build for production
npm run build

# Test production build locally
npx serve -s build -l 3000
```

## 🔧 Development

### Available Scripts

| Script | Purpose | Usage |
|--------|---------|--------|
| `npm start` | Start development server with hot reload | `npm start` |
| `npm run build` | Build optimized production bundle | `npm run build` |
| `npm test` | Run test suite with Jest | `npm test` |
| `npm run eject` | Eject from Create React App | `npm run eject` |

### Code Quality Tools
```bash
# Install additional dev dependencies
npm install --save-dev @typescript-eslint/eslint-plugin @typescript-eslint/parser prettier

# Run linting
npx eslint src/

# Format code
npx prettier --write src/
```

## 📁 Project Structure

```
prototype-frontend/
├── public/                          # Static assets
│   ├── index.html                   # HTML template
│   ├── favicon.ico                  # Favicon
│   └── manifest.json                # PWA manifest
├── src/
│   ├── components/                  # Reusable React components
│   │   ├── AnimatedChatIcon.tsx     # Animated chat icon component
│   │   ├── ChatBot.tsx              # AI chatbot interface
│   │   ├── StatusDashboard.tsx      # Service status monitoring
│   │   └── WebhookForm.tsx          # Webhook trigger form
│   ├── services/                    # API and external services
│   │   └── api.ts                   # API client for Spring Boot backend
│   ├── types/                       # TypeScript type definitions
│   │   └── chat.ts                  # Chat-related interfaces
│   ├── App.tsx                      # Main application component
│   ├── App.css                      # Global styles
│   ├── index.tsx                    # Application entry point
│   └── index.css                    # Global CSS imports
├── package.json                     # Dependencies and scripts
├── tsconfig.json                    # TypeScript configuration
├── postcss.config.js                # PostCSS configuration
├── README.md                        # This file
└── .gitignore                       # Git ignore patterns
```

## 🤖 AI Chatbot Implementation

### Features
- **Intelligent Responses**: Powered by n8n workflows with AI integration
- **Conversation Memory**: Maintains context across messages
- **Markdown Support**: Rich text formatting in responses
- **Real-time Updates**: Live typing indicators and instant responses
- **Error Recovery**: Graceful handling of API failures

### Technical Implementation

#### State Management
```typescript
interface ChatState {
  messages: ChatMessage[];
  inputMessage: string;
  isLoading: boolean;
  conversationId: string;
  isMinimized: boolean;
  isOpen: boolean;
  error: string | null;
}
```

#### Message Flow
1. User types message → React state update
2. Message sent to Spring Boot API → API validation
3. Spring Boot forwards to n8n workflow → AI processing
4. AI response returns through n8n → Spring Boot formatting
5. Formatted response sent to React → UI update

## 🔍 API Reference

### Frontend API Client (`src/services/api.ts`)

#### Available Methods

##### Health Check
```typescript
ApiService.checkHealth(): Promise<ApiResponse>
```
- **Purpose**: Verify Spring Boot service connectivity
- **Returns**: Service status and response time

##### Trigger Webhook
```typescript
ApiService.triggerWebhook(data: WebhookRequest, apiKey?: string): Promise<ApiResponse>
```
- **Parameters**:
  - `data`: Form data to send to n8n
  - `apiKey`: Optional API key for authentication
- **Returns**: Webhook execution result

##### Chat Operations
```typescript
// Send chat message
ApiService.sendChatMessage(data: ChatRequest): Promise<ChatResponse>

// Start new conversation
ApiService.startNewConversation(): Promise<ApiResponse>

// Get conversation history
ApiService.getChatHistory(conversationId: string): Promise<ApiResponse>
```

### Request/Response Examples

#### Webhook Trigger Request
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "message": "Hello from the frontend!",
  "data": {
    "userId": 12345,
    "department": "Engineering",
    "priority": "high"
  }
}
```

#### Chat Request
```json
{
  "message": "What can you help me with?",
  "conversationId": "conv_abc123",
  "userId": "user_xyz789"
}
```

## 🐛 Troubleshooting

### Common Issues and Solutions

#### 1. CORS Errors
**Symptoms**: Browser console shows CORS policy errors
```
Access to fetch at 'http://localhost:8080/api/n8n/health' from origin 'http://localhost:3000' has been blocked by CORS policy
```

**Solutions**:
```bash
# Option 1: Update Spring Boot CORS configuration
# In application.properties:
cors.allowed.origins=http://localhost:3000,http://localhost:3001

# Option 2: Use proxy in package.json (already configured)
"proxy": "http://localhost:8080"

# Option 3: Start Spring Boot with CORS enabled
java -jar backend.jar --cors.allowed.origins=http://localhost:3000
```

#### 2. API Connection Failed
**Symptoms**: "Failed to connect to Spring Boot service" error

**Diagnosis Steps**:
```bash
# Check if Spring Boot is running
curl http://localhost:8080/api/n8n/health

# Check network connectivity
ping localhost

# Verify port availability
netstat -an | grep 8080
```

**Solutions**:
- Ensure Spring Boot is running on port 8080
- Check firewall settings
- Verify API endpoint URLs in `src/services/api.ts`

#### 3. N8N Connection Issues
**Symptoms**: "Failed to test n8n connection" or workflow execution errors

**Diagnosis**:
```bash
# Check n8n status
curl http://localhost:5678/healthz

# Test webhook directly
curl -X POST http://localhost:5678/webhook/your-webhook-path \
  -H "Content-Type: application/json" \
  -d '{"test": "data"}'

# Check n8n logs
docker logs n8n_container_name
```

**Solutions**:
- Verify n8n is running on port 5678
- Check workflow activation status
- Validate webhook URLs in Spring Boot configuration
- Review n8n authentication settings

#### 4. Build Errors
**Symptoms**: `npm run build` fails with TypeScript or dependency errors

**Common Solutions**:
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clear npm cache
npm cache clean --force

# Update dependencies
npm update

# Check for TypeScript errors
npx tsc --noEmit
```

### Debug Mode

#### Enable Debug Logging
```typescript
// In src/services/api.ts, enable debug mode
const DEBUG_MODE = process.env.REACT_APP_DEBUG_MODE === 'true';

if (DEBUG_MODE) {
  console.log('API Request:', config);
  console.log('API Response:', response);
}
```

#### Browser Developer Tools
1. **Open DevTools**: Press F12 or right-click → Inspect
2. **Console Tab**: View JavaScript errors and API logs
3. **Network Tab**: Monitor API requests and responses
4. **Application Tab**: Check local storage and service workers

## 🚢 Deployment

### Production Build

#### 1. Environment Configuration
```bash
# Create production environment file
cat > .env.production << EOF
REACT_APP_API_BASE_URL=https://api.yourdomain.com
REACT_APP_N8N_URL=https://n8n.yourdomain.com
REACT_APP_ENVIRONMENT=production
REACT_APP_DEBUG_MODE=false
GENERATE_SOURCEMAP=false
EOF
```

#### 2. Build Optimization
```bash
# Build for production
npm run build

# Test production build locally
npx serve -s build
```

### Deployment Options

#### Option 1: Static Hosting (Netlify)
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Build and deploy
npm run build
netlify deploy --prod --dir=build

# Configure redirects for SPA
echo '/*    /index.html   200' > build/_redirects
```

#### Option 2: Docker Production
```dockerfile
# Multi-stage Dockerfile for production
FROM node:18-alpine as build

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production --silent

COPY . .
RUN npm run build

# Production stage with Nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### Option 3: AWS S3 + CloudFront
```bash
# Install AWS CLI
aws configure

# Create S3 bucket
aws s3 mb s3://your-app-bucket

# Sync build files
aws s3 sync build/ s3://your-app-bucket --delete

# Configure S3 for static website hosting
aws s3 website s3://your-app-bucket --index-document index.html --error-document index.html
```

### Environment Variables for Production

```bash
# Production Spring Boot configuration
export SPRING_PROFILES_ACTIVE=prod
export N8N_BASE_URL=https://n8n.yourdomain.com
export CORS_ALLOWED_ORIGINS=https://yourdomain.com

# N8N production configuration
export N8N_PROTOCOL=https
export N8N_HOST=n8n.yourdomain.com
export N8N_PORT=443
export WEBHOOK_URL=https://n8n.yourdomain.com/
export DB_SQLITE_POOL_SIZE=10
export N8N_RUNNERS_ENABLED=true
```

## 🤝 Contributing

### Development Guidelines

#### Code Style
- **TypeScript**: Use strict mode with proper type definitions
- **Components**: Functional components with hooks
- **Styling**: Bootstrap classes with custom CSS when needed
- **File Naming**: PascalCase for components, camelCase for utilities

#### Commit Convention
```bash
# Format: type(scope): description
git commit -m "feat(chat): add message history persistence"
git commit -m "fix(api): handle network timeout errors"
git commit -m "docs(readme): update installation instructions"
```

#### Pull Request Process
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🚀 Quick Start Checklist

- [ ] Node.js 16+ installed
- [ ] n8n running on port 5678
- [ ] Spring Boot backend on port 8080
- [ ] Environment variables configured
- [ ] Dependencies installed (`npm install`)
- [ ] Development server started (`npm start`)
- [ ] Application accessible at http://localhost:3000

**Need help?** Check the [troubleshooting section](#-troubleshooting) or open an issue on GitHub.

## 📄 License

This project is part of the N8N Integration prototype system. Please refer to the LICENSE file for detailed licensing information.

---

## 📞 Support & Documentation

### Getting Help
- **GitHub Issues**: For bug reports and feature requests
- **Documentation**: 
  - [React](https://react.dev/)
  - [TypeScript](https://www.typescriptlang.org/docs/)
  - [Bootstrap](https://getbootstrap.com/docs/5.3/)
  - [n8n](https://docs.n8n.io/)

### Community
- **Stack Overflow**: Tag your questions with `react`, `typescript`, `n8n`
- **Discord/Slack**: Join the community channels for real-time help

---

*This README is maintained by the development team. Last updated: September 2025*

**🎯 Ready to get started?** Follow the [Quick Start Checklist](#-quick-start-checklist) above!
