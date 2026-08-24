package com.intellispace.backend.identity.application.port.in;

public interface LoginUseCase {
    AuthResult login(LoginCommand command);
}