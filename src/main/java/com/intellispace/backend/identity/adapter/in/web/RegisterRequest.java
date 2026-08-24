package com.intellispace.backend.identity.adapter.in.web;

import jakarta.validation.constraints.*;

public record RegisterRequest(@Email @NotBlank String email, @NotBlank @Size(min = 8) String password, String displayName) {}


