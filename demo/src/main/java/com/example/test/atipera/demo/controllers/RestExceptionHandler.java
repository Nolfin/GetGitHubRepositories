package com.example.test.atipera.demo.controllers;

import com.example.test.atipera.demo.dto.ErrorDTO;
import com.example.test.atipera.demo.exceptions.RateLimitExceededException;
import com.example.test.atipera.demo.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorDTO> rateLimitHandler(RateLimitExceededException ex, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS.value()).body(new ErrorDTO(HttpStatus.TOO_MANY_REQUESTS.value(), ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDTO> userNotFoundHandler(UserNotFoundException ex, WebRequest webRequest) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(new ErrorDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }
}