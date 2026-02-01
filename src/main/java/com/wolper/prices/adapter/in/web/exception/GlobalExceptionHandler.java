package com.wolper.prices.adapter.in.web.exception;

import com.wolper.prices.adapter.in.web.dto.ErrorResponse;
import com.wolper.prices.domain.exception.PriceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 * Centraliza el manejo de errores y proporciona respuestas consistentes.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Maneja excepciones cuando no se encuentra un precio.
     */
    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFound(
            PriceNotFoundException ex, HttpServletRequest request) {
        
        log.warn("Precio no encontrado: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Maneja violaciones de constraints de validación.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.warn("Violación de constraints: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            fieldErrors.put(fieldName, errorMessage);
        }
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error de validación")
                .path(request.getRequestURI())
                .fields(fieldErrors)
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Maneja errores de validación de argumentos del método.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("Argumentos de método inválidos: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error de validación")
                .path(request.getRequestURI())
                .fields(fieldErrors)
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Maneja parámetros faltantes en la request.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        log.warn("Parámetro faltante: {}", ex.getParameterName());
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(String.format("Parámetro requerido '%s' no está presente", ex.getParameterName()))
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Maneja errores de tipo de argumento incompatible.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.warn("Tipo de argumento incompatible: {}", ex.getMessage());
        
        String message = String.format("El parámetro '%s' debe ser de tipo %s", 
                ex.getName(), 
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconocido");
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }
    
    /**
     * Maneja excepciones genéricas no controladas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Error interno del servidor", ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Error interno del servidor")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
