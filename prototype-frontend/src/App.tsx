import React from 'react';
import StatusDashboard from './components/StatusDashboard';
import WebhookForm from './components/WebhookForm';
import ChatBot from './components/ChatBot';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

function App() {
  return (
    <div className="min-vh-100 bg-light">
      {/* Header */}
      <header className="app-header">
        <div className="container">
          <div className="text-center">
            <h1 className="display-4 fw-bold mb-4">
              N8N Integration Platform
            </h1>
            <p className="lead">
              Connect your React frontend to n8n workflows through a Spring Boot API. 
              Monitor service health, send data, get real-time responses, and chat with our AI assistant.
            </p>
          </div>
        </div>
      </header>

      <div className="container py-4">
        {/* Status Dashboard */}
        <StatusDashboard />

        {/* Webhook Form */}
        <WebhookForm />
      </div>

      {/* Footer */}
      <footer className="app-footer">
        <div className="container">
          <div className="text-center">
            <p className="mb-1">React → Spring Boot → N8N Integration</p>
            <p className="mb-0">Built with React, TypeScript, and Bootstrap</p>
          </div>
        </div>
      </footer>

      {/* AI Chatbot */}
      <ChatBot />
    </div>
  );
}

export default App;
