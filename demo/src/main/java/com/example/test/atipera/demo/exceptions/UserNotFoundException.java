package com.example.test.atipera.demo.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(Throwable cause) {
        super("User not found", cause);
    }
}