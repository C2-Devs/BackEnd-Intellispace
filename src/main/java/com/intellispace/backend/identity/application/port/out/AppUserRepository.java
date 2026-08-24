package com.intellispace.backend.identity.application.port.out;

import com.intellispace.backend.identity.domain.AppUser;
import java.util.Optional;

public interface AppUserRepository {
    AppUser save(AppUser user);
    Optional<AppUser> findByEmail(String email);
}