package com.intellispace.backend.catalog.application.service;

import com.intellispace.backend.catalog.adapter.out.persistence.CatalogItemJpaRepository;
import com.intellispace.backend.catalog.application.port.in.CatalogItemQueryUseCase;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class CatalogItemQueryService implements CatalogItemQueryUseCase {

    private final CatalogItemJpaRepository jpaRepository;

    public CatalogItemQueryService(CatalogItemJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsById(UUID catalogItemId) {
        return jpaRepository.existsById(catalogItemId);
    }
}