package com.intellispace.backend.identity.application.service;

import com.intellispace.backend.common.security.JwtService;
import com.intellispace.backend.identity.application.port.in.*;
import com.intellispace.backend.identity.application.port.out.AppUserRepository;
import com.intellispace.backend.identity.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthApplicationService implements RegisterUserUseCase, LoginUseCase {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthApplicationService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResult register(RegisterUserCommand command) {
        if (appUserRepository.findByEmail(command.email()).isPresent()) {
            throw new EmailAlreadyRegisteredException(command.email());
        }
        AppUser user = AppUser.register(command.email(), passwordEncoder.encode(command.rawPassword()), command.displayName());
        AppUser saved = appUserRepository.save(user);
        return new AuthResult(saved.getId(), jwtService.issueToken(saved.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult login(LoginCommand command) {
        AppUser user = appUserRepository.findByEmail(command.email()).orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return new AuthResult(user.getId(), jwtService.issueToken(user.getId()));
    }
}