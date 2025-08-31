package com.example.wallet_app.config;


import com.example.wallet_app.ErrorResponse;
import com.example.wallet_app.exceptions.BadRequestException;
import com.example.wallet_app.exceptions.ConflictException;
import com.example.wallet_app.exceptions.InsufficientWalletException;
import com.example.wallet_app.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(NotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "REQ_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflictException(ConflictException ex) {
        ErrorResponse error = new ErrorResponse(
                "CONFLICT",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

//    @ExceptionHandler(AuthException.class)
//    public ResponseEntity<Object> handleAuthException(AuthException ex) {
//        ErrorResponse error = new ErrorResponse(
//                "AUTH_ERROR",
//                ex.getMessage(),
//                HttpStatus.BAD_REQUEST.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(AuthCredentialsException.class)
//    public ResponseEntity<Object> handleAuthenticationException(AuthCredentialsException ex) {
//        ErrorResponse error = new ErrorResponse(
//                "AUTH_ERROR",
//                ex.getMessage(),
//                HttpStatus.BAD_REQUEST.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    public ResponseEntity<Object> handleMissingParameterException(MissingServletRequestParameterException ex) {
//        ErrorResponse error = new ErrorResponse(
//                "REQ_MISSING_FIELD",
//                "Required parameter is missing: " + ex.getParameterName(),
//                HttpStatus.UNPROCESSABLE_ENTITY.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
//    }
//
//
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
//        ErrorResponse error = new ErrorResponse(
//                "REQ_NOT_READABLE",
//                ex.getMessage(),
//                HttpStatus.UNPROCESSABLE_ENTITY.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
//        StringBuilder errorMessage = new StringBuilder("Validation failed for fields: ");
//        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
//            errorMessage.append(fieldError.getField()).append(" (").append(fieldError.getDefaultMessage())
//                    .append("), ");
//        }
//        ErrorResponse error = new ErrorResponse(
//                "REQ_VALIDATION_FAILED",
//                errorMessage.toString(),
//                HttpStatus.UNPROCESSABLE_ENTITY.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
//    }
//
//    @ExceptionHandler(HandlerMethodValidationException.class)
//    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
//        ErrorResponse error = new ErrorResponse(
//                "REQ_VALIDATION_FAILED",
//                "Validation failed for fields: " + ex.getMessage(),
//                HttpStatus.UNPROCESSABLE_ENTITY.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
//        );
//
//        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
//    }
//
//    @ExceptionHandler(UnauthorizedException.class)
//    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
//        ErrorResponse error = new ErrorResponse(
//                "REQ_AUTHORIZATION_FAILED",
//                "Unauthorized: Please provide valid credentials",
//                HttpStatus.UNAUTHORIZED.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
//    }
//
//
//    @ExceptionHandler(NoResourceFoundException.class)
//    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
//        ErrorResponse error = new ErrorResponse(
//                "REQ_RESOURCE_NOT_FOUND",
//                "Resource not found: " + ex.getMessage(),
//                HttpStatus.NOT_FOUND.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
//
//        String message = ex.getConstraintViolations().stream()
//                .map(violation -> {
//                    String path = violation.getPropertyPath().toString();
//                    String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
//                    return field + ": " + violation.getMessage();
//                })
//                .collect(Collectors.joining(", "));
//
//        ErrorResponse error = new ErrorResponse(
//                "BAD_REQUEST",
//                "Bad Request: " + message,
//                HttpStatus.BAD_REQUEST.value(),
//                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
    @ExceptionHandler(InsufficientWalletException.class)
    public ResponseEntity<?> handleInsufficientWalletException(InsufficientWalletException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "REQ_WALLET_INSUFFICIENT",
                        HttpStatus.BAD_REQUEST.value(),
                        "error", ex.getMessage(),
                        "time", Instant.now().toString())
                );
    }
//
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        log.error("An unexpected error occurred.", ex);
        ErrorResponse error = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
