export interface ChatMessage {
  id: string;
  content: string;
  role: 'user' | 'assistant';
  timestamp: Date;
  isLoading?: boolean;
}

export interface ChatRequest {
  message: string;
  conversationId?: string;
  userId?: string;
}

export interface ChatResponse {
  success: boolean;
  message: string;
  response?: string;
  conversationId?: string;
  timestamp: string;
  data?: any;
}

export interface ChatSession {
  id: string;
  title: string;
  messages: ChatMessage[];
  createdAt: Date;
  updatedAt: Date;
}


