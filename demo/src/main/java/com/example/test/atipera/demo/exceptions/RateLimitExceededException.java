package com.example.test.atipera.demo.exceptions;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super("Rate limit has been exceeded");
    }

    public RateLimitExceededException(Throwable cause) {
        super("Rate limit has been exceeded", cause);
    }
}