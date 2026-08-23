package com.intellispace.backend.catalog.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CatalogItemJpaRepository extends JpaRepository<CatalogItemEntity, UUID> {
}