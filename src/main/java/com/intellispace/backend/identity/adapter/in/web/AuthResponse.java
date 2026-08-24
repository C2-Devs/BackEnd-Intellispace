package com.intellispace.backend.identity.adapter.in.web;

import java.util.UUID;

public record AuthResponse(UUID userId, String token) {}
