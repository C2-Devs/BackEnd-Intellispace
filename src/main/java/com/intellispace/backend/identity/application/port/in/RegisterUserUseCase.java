package com.intellispace.backend.identity.application.port.in;

public interface RegisterUserUseCase {
    AuthResult register(RegisterUserCommand command);
}
