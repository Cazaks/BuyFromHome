package com.market.BuyFromHome.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    ResponseEntity<ErrorResponse> handleAppException(
            AppException ex, HttpServletRequest request){

        log.warn("AppException: {}", ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(
                ErrorResponse.builder()
                        .status(ex.getStatus().value())
                        .error(ex.getStatus().name())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request){

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("VALIDATION_FAILED")
                        .message("One or more fields are invalid")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .validationErrors(errors)
                        .build()
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ErrorResponse.builder()
                                .status(HttpStatus.FORBIDDEN.value())
                                .error(HttpStatus.FORBIDDEN.name())
                                .message("Access Denied")
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .error(HttpStatus.UNAUTHORIZED.name())
                                .message("Invalid username or password")
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(
                        ErrorResponse.builder()
                                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                                .error(HttpStatus.METHOD_NOT_ALLOWED.name())
                                .message(ex.getMessage())
                                .path(request.getRequestURI())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

}
