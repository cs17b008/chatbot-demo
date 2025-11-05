import React, { useState, useEffect } from 'react';
import { Activity, Server, Zap, RefreshCw, MessageCircle } from 'lucide-react';
import { ApiService } from '../services/api';

interface ServiceStatus {
  name: string;
  status: 'checking' | 'online' | 'offline';
  message: string;
  icon: React.ReactNode;
}

const StatusDashboard: React.FC = () => {
  const [services, setServices] = useState<ServiceStatus[]>([
    {
      name: 'Spring Boot API',
      status: 'checking',
      message: 'Checking connection...',
      icon: <Server className="w-5 h-5" />
    },
    {
      name: 'N8N Workflow',
      status: 'checking',
      message: 'Checking connection...',
      icon: <Zap className="w-5 h-5" />
    },
    {
      name: 'AI Chatbot',
      status: 'checking',
      message: 'Checking chatbot service...',
      icon: <MessageCircle className="w-5 h-5" />
    }
  ]);

  const [lastChecked, setLastChecked] = useState<Date | null>(null);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const checkServices = async () => {
    setIsRefreshing(true);
    
    // Check Spring Boot health
    try {
      const healthResponse = await ApiService.checkHealth();
      setServices(prev => prev.map(service => 
        service.name === 'Spring Boot API' 
          ? {
              ...service,
              status: healthResponse.success ? 'online' : 'offline',
              message: healthResponse.message
            }
          : service
      ));
    } catch (error) {
      setServices(prev => prev.map(service => 
        service.name === 'Spring Boot API' 
          ? {
              ...service,
              status: 'offline',
              message: 'Connection failed'
            }
          : service
      ));
    }

    // Check N8N connection
    try {
      const n8nResponse = await ApiService.testN8nConnection();
      setServices(prev => prev.map(service => 
        service.name === 'N8N Workflow' 
          ? {
              ...service,
              status: n8nResponse.success ? 'online' : 'offline',
              message: n8nResponse.message
            }
          : service
      ));
    } catch (error) {
      setServices(prev => prev.map(service => 
        service.name === 'N8N Workflow' 
          ? {
              ...service,
              status: 'offline',
              message: 'Connection failed'
            }
          : service
      ));
    }

    // Check AI Chatbot service (assumes it uses the same backend)
    try {
      // For now, we'll check if Spring Boot is healthy as the chatbot relies on it
      const chatbotStatus = await ApiService.checkHealth();
      setServices(prev => prev.map(service => 
        service.name === 'AI Chatbot' 
          ? {
              ...service,
              status: chatbotStatus.success ? 'online' : 'offline',
              message: chatbotStatus.success ? 'Chatbot ready' : 'Chatbot unavailable'
            }
          : service
      ));
    } catch (error) {
      setServices(prev => prev.map(service => 
        service.name === 'AI Chatbot' 
          ? {
              ...service,
              status: 'offline',
              message: 'Chatbot unavailable'
            }
          : service
      ));
    }

    setLastChecked(new Date());
    setIsRefreshing(false);
  };

  useEffect(() => {
    checkServices();
  }, []);

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'online':
        return '●';
      case 'offline':
        return '●';
      case 'checking':
        return '◐';
      default:
        return '○';
    }
  };

  return (
    <div className="row justify-content-center mb-4">
      <div className="col-lg-8">
        <div className="card shadow-lg mb-4">
          <div className="card-body p-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
              <div className="d-flex align-items-center">
                <Activity className="me-2 icon-md text-primary" />
                <h2 className="h3 mb-0 fw-bold">Service Status</h2>
              </div>
              <button
                onClick={checkServices}
                disabled={isRefreshing}
                className="btn btn-primary d-flex align-items-center"
              >
                <RefreshCw className={`me-2 icon-sm ${isRefreshing ? 'spinner-border spinner-border-sm' : ''}`} />
                Refresh
              </button>
            </div>

            <div className="row g-3">
              {services.map((service, index) => (
                <div key={index} className="col-12">
                  <div className="service-card p-3 rounded d-flex justify-content-between align-items-center">
                    <div className="d-flex align-items-center">
                      <div className="text-muted me-3">
                        {service.icon}
                      </div>
                      <div>
                        <h5 className="mb-1 fw-semibold">{service.name}</h5>
                        <p className="mb-0 text-muted small">{service.message}</p>
                      </div>
                    </div>
                    <div className={`status-badge ${service.status}`}>
                      <span className={`status-indicator status-${service.status}`}></span>
                      {service.status.charAt(0).toUpperCase() + service.status.slice(1)}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {lastChecked && (
              <div className="mt-3 text-center">
                <small className="text-muted">
                  Last checked: {lastChecked.toLocaleTimeString()}
                </small>
              </div>
            )}

            {services.every(s => s.status === 'online') && (
              <div className="mt-3">
                <div className="alert alert-success d-flex align-items-center" role="alert">
                  <div className="text-center w-100">
                    🎉 All services are running! You can send data to n8n workflows and chat with the AI assistant.
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default StatusDashboard;
