package com.intellispace.backend.identity.application.port.in;
import java.util.UUID;

public record AuthResult(UUID userId, String token) {}