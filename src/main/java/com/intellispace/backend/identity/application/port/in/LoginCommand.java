package com.intellispace.backend.identity.application.port.in;

public record LoginCommand(String email, String rawPassword) {}
