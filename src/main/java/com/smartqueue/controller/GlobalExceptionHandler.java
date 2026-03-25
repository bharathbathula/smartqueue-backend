package com.smartqueue.controller;

import com.smartqueue.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        System.err.println("Validation error: " + errors);
        return ResponseEntity.badRequest().body(new MessageResponse("Validation error: " + errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex) {
        System.err.println("Global error caught: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.internalServerError().body(new MessageResponse("Error: " + ex.getMessage()));
    }
}
