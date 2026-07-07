package com.example.ZenDesk.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<Map<String,String>> exception1(Exception e)
    {
        Map<String,String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,String>> handleResoure(Exception e)
    {
        Map<String,String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> validateField(MethodArgumentNotValidException ex)
    {
        Map<String,String> error = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> error.put(err.getField(),err.getDefaultMessage()));
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String>> handleDuplicate(DataIntegrityViolationException ex)
    {
        Map<String,String> error = new HashMap<>();
        error.put("message", "Email is already registered. Kindly use different email!");
        return ResponseEntity.status(400).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String,String>> handleUnautorized(BadCredentialsException ex)
    {
        Map<String,String> error = new HashMap<>();
        error.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleGenericException(Exception ex)
    {
        Map<String,String> error = new HashMap<>();
        error.put("message", "Something went wrong: "+ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    
}
