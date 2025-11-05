import React, { useState } from 'react';
import { Send, CheckCircle, AlertCircle, Loader2 } from 'lucide-react';
import { ApiService, WebhookRequest, ApiResponse } from '../services/api';

interface FormData {
  name: string;
  email: string;
  message: string;
  additionalData: string;
}

const WebhookForm: React.FC = () => {
  const [formData, setFormData] = useState<FormData>({
    name: '',
    email: '',
    message: '',
    additionalData: ''
  });

  const [isLoading, setIsLoading] = useState(false);
  const [response, setResponse] = useState<ApiResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    setResponse(null);

    try {
      // Prepare the webhook request
      const webhookData: WebhookRequest = {
        name: formData.name,
        email: formData.email,
        message: formData.message,
        data: formData.additionalData ? JSON.parse(formData.additionalData) : undefined
      };

      // Trigger the webhook
      const result = await ApiService.triggerWebhook(webhookData);
      setResponse(result);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      email: '',
      message: '',
      additionalData: ''
    });
    setResponse(null);
    setError(null);
  };

  return (
    <div className="row justify-content-center">
      <div className="col-lg-8">
        <div className="card form-card shadow-lg">
          <div className="card-body p-4">
            <div className="mb-4">
              <h2 className="h3 fw-bold mb-2">N8N Webhook Trigger</h2>
              <p className="text-muted">Send data to your n8n workflow via Spring Boot API</p>
            </div>

            <form onSubmit={handleSubmit}>
              {/* Name Field */}
              <div className="mb-3">
                <label htmlFor="name" className="form-label fw-semibold">
                  Full Name *
                </label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  required
                  value={formData.name}
                  onChange={handleInputChange}
                  className="form-control form-control-lg"
                  placeholder="Enter your full name"
                />
              </div>

              {/* Email Field */}
              <div className="mb-3">
                <label htmlFor="email" className="form-label fw-semibold">
                  Email Address *
                </label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  required
                  value={formData.email}
                  onChange={handleInputChange}
                  className="form-control form-control-lg"
                  placeholder="Enter your email address"
                />
              </div>

              {/* Message Field */}
              <div className="mb-3">
                <label htmlFor="message" className="form-label fw-semibold">
                  Message
                </label>
                <textarea
                  id="message"
                  name="message"
                  rows={4}
                  value={formData.message}
                  onChange={handleInputChange}
                  className="form-control"
                  placeholder="Enter your message (optional)"
                />
              </div>

              {/* Additional Data Field */}
              <div className="mb-4">
                <label htmlFor="additionalData" className="form-label fw-semibold">
                  Additional Data (JSON)
                </label>
                <textarea
                  id="additionalData"
                  name="additionalData"
                  rows={3}
                  value={formData.additionalData}
                  onChange={handleInputChange}
                  className="form-control font-monospace"
                  placeholder='{"key": "value", "example": true}'
                />
                <div className="form-text">
                  Optional: Enter valid JSON data to send additional information
                </div>
              </div>

              {/* Submit Button */}
              <div className="d-flex gap-3">
                <button
                  type="submit"
                  disabled={isLoading}
                  className="btn btn-primary btn-lg flex-grow-1 d-flex align-items-center justify-content-center"
                >
                  {isLoading ? (
                    <>
                      <div className="spinner-border spinner-border-sm me-2" role="status">
                        <span className="visually-hidden">Loading...</span>
                      </div>
                      Sending...
                    </>
                  ) : (
                    <>
                      <Send className="me-2 icon-sm" />
                      Send to N8N
                    </>
                  )}
                </button>

                <button
                  type="button"
                  onClick={resetForm}
                  className="btn btn-outline-secondary btn-lg"
                >
                  Reset
                </button>
              </div>
            </form>

            {/* Response Display */}
            {response && (
              <div className="mt-4 fade-in">
                <div className="response-card success p-4">
                  <div className="d-flex align-items-center mb-2">
                    <CheckCircle className="me-2 icon-md" />
                    <h4 className="h5 mb-0 fw-bold">Success!</h4>
                  </div>
                  <p className="mb-3">{response.message}</p>
                  <div className="bg-white p-3 rounded border">
                    <h5 className="h6 fw-semibold mb-2">Response Data:</h5>
                    <pre className="mb-0 small">
                      {JSON.stringify(response, null, 2)}
                    </pre>
                  </div>
                </div>
              </div>
            )}

            {/* Error Display */}
            {error && (
              <div className="mt-4 fade-in">
                <div className="response-card error p-4">
                  <div className="d-flex align-items-center mb-2">
                    <AlertCircle className="me-2 icon-md" />
                    <h4 className="h5 mb-0 fw-bold">Error</h4>
                  </div>
                  <p className="mb-0">{error}</p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default WebhookForm;
