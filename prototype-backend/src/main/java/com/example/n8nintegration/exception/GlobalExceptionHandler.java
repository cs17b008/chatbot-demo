package com.example.n8nintegration.exception;

import com.example.n8nintegration.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String requestId = UUID.randomUUID().toString();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.warn("Validation error - RequestID: {}, Errors: {}", requestId, errors);

        return ResponseEntity.badRequest()
            .body(new ApiResponse(false, "Validation failed", errors, requestId));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponse> handleRestClientException(RestClientException ex) {
        String requestId = UUID.randomUUID().toString();
        logger.error("REST client error - RequestID: {}, Error: {}", requestId, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(new ApiResponse(false, "External service error: " + ex.getMessage(), null, requestId));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        String requestId = UUID.randomUUID().toString();
        logger.error("Runtime error - RequestID: {}, Error: {}", requestId, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse(false, "Internal server error: " + ex.getMessage(), null, requestId));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        String requestId = UUID.randomUUID().toString();
        logger.error("Unexpected error - RequestID: {}, Error: {}", requestId, ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ApiResponse(false, "An unexpected error occurred", null, requestId));
    }
}
