package com.intellispace.backend.identity.adapter.out.persistence;

import com.intellispace.backend.identity.domain.AppUser;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {

    public AppUserEntity toNewEntity(AppUser user) {
        AppUserEntity entity = AppUserEntity.builder()
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .displayName(user.getDisplayName())
                .build();
        entity.setId(user.getId()); // id is managed by the domain, not Hibernate
        return entity;
    }

    public AppUser toDomain(AppUserEntity entity) {
        return AppUser.reconstruct(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getDisplayName());
    }
}