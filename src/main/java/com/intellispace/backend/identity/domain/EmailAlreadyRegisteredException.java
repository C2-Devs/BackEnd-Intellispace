package com.intellispace.backend.identity.domain;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("An account already exists for: " + email);
    }
}

