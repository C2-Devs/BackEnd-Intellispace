package com.intellispace.backend.identity.adapter.in.web;

import com.intellispace.backend.identity.application.port.in.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        var result = registerUserUseCase.register(new RegisterUserCommand(request.email(), request.password(), request.displayName()));
        return new AuthResponse(result.userId(), result.token());
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        var result = loginUseCase.login(new LoginCommand(request.email(), request.password()));
        return new AuthResponse(result.userId(), result.token());
    }
}