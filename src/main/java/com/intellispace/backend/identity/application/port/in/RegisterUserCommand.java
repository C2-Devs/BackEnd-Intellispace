package com.intellispace.backend.identity.application.port.in;

public record RegisterUserCommand(String email, String rawPassword, String displayName) {}
