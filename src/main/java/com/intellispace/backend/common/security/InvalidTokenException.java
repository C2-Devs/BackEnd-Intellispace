package com.intellispace.backend.common.security;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(Throwable cause) {
        super("Invalid or expired token", cause);
    }
}