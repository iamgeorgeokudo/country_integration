package com.ncba.countryintegration.exception;

import com.ncba.countryintegration.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleNotFound(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SoapIntegrationException.class)
    public ResponseEntity<ApiError> handleSoapException(
            SoapIntegrationException ex,
            HttpServletRequest request) {

        log.error("External SOAP integration failed", ex);

        return buildError(
                HttpStatus.BAD_GATEWAY,
                "The external country information service is currently unavailable.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Request validation failed");

        return buildError(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableRequest(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "The request body is missing or contains invalid JSON.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid value provided for parameter: " + ex.getName(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.warn("Database constraint violation: {}", ex.getMessage());

        return buildError(
                HttpStatus.CONFLICT,
                "The country conflicts with an existing record or database constraint.",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected application error", ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                request.getRequestURI()
        );
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            String path) {

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        return ResponseEntity.status(status).body(error);
    }
}