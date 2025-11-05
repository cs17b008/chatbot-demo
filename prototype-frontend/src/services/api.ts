import axios, { type AxiosResponse } from 'axios';
import { ChatRequest, ChatResponse } from '../types/chat';

// Base configuration for API calls
// Using relative URL since we're proxying through React dev server
const API_BASE_URL = '/api/n8n';

// Create axios instance with default configuration
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
});

// Types for API requests and responses
export interface WebhookRequest {
  name: string;
  email: string;
  message?: string;
  data?: any;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
  requestId?: string;
  timestamp: string;
}

// API service class
export class ApiService {
  
  /**
   * Check if the Spring Boot service is healthy
   */
  static async checkHealth(): Promise<ApiResponse> {
    try {
      const response: AxiosResponse<ApiResponse> = await apiClient.get('/health');
      return response.data;
    } catch (error) {
      console.error('Health check failed:', error);
      throw new Error('Failed to connect to Spring Boot service');
    }
  }

  /**
   * Test the connection to n8n
   */
  static async testN8nConnection(): Promise<ApiResponse> {
    try {
      const response: AxiosResponse<ApiResponse> = await apiClient.get('/test');
      return response.data;
    } catch (error) {
      console.error('N8n connection test failed:', error);
      throw new Error('Failed to test n8n connection');
    }
  }

  /**
   * Trigger the n8n webhook with provided data
   */
  static async triggerWebhook(data: WebhookRequest, apiKey?: string): Promise<ApiResponse> {
    try {
      const headers: any = {};
      if (apiKey) {
        headers['X-API-Key'] = apiKey;
      }

      const response: AxiosResponse<ApiResponse> = await apiClient.post('/trigger', data, {
        headers,
      });
      
      return response.data;
    } catch (error: any) {
      console.error('Webhook trigger failed:', error);
      
      if (error.response?.data) {
        throw new Error(error.response.data.message || 'Failed to trigger webhook');
      }
      
      throw new Error('Failed to trigger webhook');
    }
  }

  /**
   * Send a chat message to the AI chatbot
   */
  static async sendChatMessage(data: ChatRequest): Promise<ChatResponse> {
    try {
      const response: AxiosResponse<ChatResponse> = await apiClient.post('/chat', data);
      return response.data;
    } catch (error: any) {
      console.error('Chat message failed:', error);
      
      if (error.response?.data) {
        throw new Error(error.response.data.message || 'Failed to send chat message');
      }
      
      throw new Error('Failed to send chat message');
    }
  }

  /**
   * Get chat conversation history
   */
  static async getChatHistory(conversationId: string): Promise<ApiResponse> {
    try {
      const response: AxiosResponse<ApiResponse> = await apiClient.get(`/chat/history/${conversationId}`);
      return response.data;
    } catch (error: any) {
      console.error('Failed to get chat history:', error);
      throw new Error('Failed to retrieve chat history');
    }
  }

  /**
   * Start a new chat conversation
   */
  static async startNewConversation(): Promise<ApiResponse> {
    try {
      const response: AxiosResponse<ApiResponse> = await apiClient.post('/chat/new');
      return response.data;
    } catch (error: any) {
      console.error('Failed to start new conversation:', error);
      throw new Error('Failed to start new conversation');
    }
  }
}

// Export default instance
export default ApiService;
