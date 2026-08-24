package com.intellispace.backend.identity.adapter.out.persistence;

import com.intellispace.backend.identity.application.port.out.AppUserRepository;
import com.intellispace.backend.identity.domain.AppUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppUserRepositoryAdapter implements AppUserRepository {

    private final AppUserJpaRepository jpaRepository;
    private final AppUserMapper mapper;

    public AppUserRepositoryAdapter(AppUserJpaRepository jpaRepository, AppUserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public AppUser save(AppUser user) {
        return mapper.toDomain(jpaRepository.save(mapper.toNewEntity(user)));
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }
}