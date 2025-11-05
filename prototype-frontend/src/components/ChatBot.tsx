import React, { useState, useEffect, useRef } from 'react';
import { Send, Bot, User, Trash2, Plus, X, Minimize2, Maximize2 } from 'lucide-react';
import { ApiService } from '../services/api';
import { ChatMessage, ChatRequest, ChatSession } from '../types/chat';
import ReactMarkdown from 'react-markdown';
import AnimatedChatIcon from './AnimatedChatIcon';

const ChatBot: React.FC = () => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [conversationId, setConversationId] = useState<string>('');
  const [isMinimized, setIsMinimized] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (isOpen && !conversationId) {
      startNewConversation();
    }
  }, [isOpen, conversationId]);

  const startNewConversation = async () => {
    try {
      const response = await ApiService.startNewConversation();
      if (response.success && response.data?.conversationId) {
        setConversationId(response.data.conversationId);
        setMessages([{
          id: 'welcome',
          content: 'Hello! I\'m your AI assistant. I\'m powered by the same n8n and Spring Boot backend that handles your webhooks. How can I help you today?',
          role: 'assistant',
          timestamp: new Date()
        }]);
      }
    } catch (error: any) {
      setError('Failed to start conversation. Please try again.');
      console.error('Failed to start conversation:', error);
    }
  };

  const sendMessage = async () => {
    if (!inputMessage.trim() || isLoading) return;

    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      content: inputMessage.trim(),
      role: 'user',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputMessage('');
    setIsLoading(true);
    setError(null);

    // Add loading message
    const loadingMessage: ChatMessage = {
      id: 'loading',
      content: 'AI is thinking...',
      role: 'assistant',
      timestamp: new Date(),
      isLoading: true
    };
    setMessages(prev => [...prev, loadingMessage]);

    try {
      const chatRequest: ChatRequest = {
        message: userMessage.content,
        conversationId: conversationId,
        userId: 'user-' + Date.now() // You can implement proper user management
      };

      const response = await ApiService.sendChatMessage(chatRequest);
      
      if (response.success && response.response) {
        const assistantMessage: ChatMessage = {
          id: (Date.now() + 1).toString(),
          content: response.response,
          role: 'assistant',
          timestamp: new Date()
        };

        // Remove loading message and add assistant response
        setMessages(prev => prev.filter(msg => msg.id !== 'loading').concat(assistantMessage));
        
        // Update conversation ID if provided
        if (response.conversationId) {
          setConversationId(response.conversationId);
        }
      } else {
        throw new Error(response.message || 'Failed to get response');
      }
    } catch (error: any) {
      setError(error.message);
      // Remove loading message
      setMessages(prev => prev.filter(msg => msg.id !== 'loading'));
      
      // Add error message
      const errorMessage: ChatMessage = {
        id: 'error-' + Date.now(),
        content: 'Sorry, I encountered an error. Please try again.',
        role: 'assistant',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const clearConversation = async () => {
    setMessages([]);
    setConversationId('');
    setError(null);
    await startNewConversation();
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const formatTime = (date: Date) => {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  if (!isOpen) {
    return (
      <div className="chatbot-toggle">
        <button
          onClick={() => setIsOpen(true)}
          className="btn btn-primary btn-lg rounded-circle chatbot-toggle-btn"
          title="Open AI Assistant"
        >
          <AnimatedChatIcon />
        </button>
      </div>
    );
  }

  return (
    <div className={`chatbot-container ${isMinimized ? 'minimized' : ''}`}>
      <div className="chatbot-header">
        <div className="d-flex align-items-center">
          <Bot className="me-2" size={20} />
          <h5 className="mb-0 fw-bold">AI Assistant</h5>
          <span className="ms-2 badge bg-success">Online</span>
        </div>
        <div className="d-flex align-items-center gap-2">
          <button
            onClick={clearConversation}
            className="btn btn-outline-light btn-sm"
            title="New Conversation"
          >
            <Plus size={16} />
          </button>
          <button
            onClick={() => setIsMinimized(!isMinimized)}
            className="btn btn-outline-light btn-sm"
            title={isMinimized ? "Maximize" : "Minimize"}
          >
            {isMinimized ? <Maximize2 size={16} /> : <Minimize2 size={16} />}
          </button>
          <button
            onClick={() => setIsOpen(false)}
            className="btn btn-outline-light btn-sm"
            title="Close"
          >
            <X size={16} />
          </button>
        </div>
      </div>

      {!isMinimized && (
        <>
          <div className="chatbot-messages">
            {messages.map((message) => (
              <div
                key={message.id}
                className={`message ${message.role} ${message.isLoading ? 'loading' : ''}`}
              >
                <div className="message-avatar">
                  {message.role === 'user' ? (
                    <User size={16} />
                  ) : (
                    <Bot size={16} />
                  )}
                </div>
                <div className="message-content">
                  <div className="message-text">
                  {message.isLoading ? (
                    <div className="d-flex align-items-center">
                        <div className="spinner-border spinner-border-sm me-2" role="status">
                        <span className="visually-hidden">Loading...</span>
                        </div>
                        {message.content}
                    </div>
                    ) : message.role === 'assistant' ? (
                    <ReactMarkdown>{message.content}</ReactMarkdown>
                    ) : (
                    message.content
                    )}
                  </div>
                  <div className="message-time">
                    {formatTime(message.timestamp)}
                  </div>
                </div>
              </div>
            ))}
            {error && (
              <div className="alert alert-danger alert-sm mx-3 mb-2">
                <small>{error}</small>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          <div className="chatbot-input">
            <div className="input-group">
              <input
                ref={inputRef}
                type="text"
                value={inputMessage}
                onChange={(e) => setInputMessage(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="Type your message..."
                className="form-control"
                disabled={isLoading}
              />
              <button
                onClick={sendMessage}
                disabled={isLoading || !inputMessage.trim()}
                className="btn btn-primary"
                type="button"
              >
                {isLoading ? (
                  <div className="spinner-border spinner-border-sm" role="status">
                    <span className="visually-hidden">Loading...</span>
                  </div>
                ) : (
                  <Send size={16} />
                )}
              </button>
            </div>
            <div className="chatbot-info">
              <small className="text-muted">
                Powered by n8n & Spring Boot • Press Enter to send
              </small>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default ChatBot;

