package com.intellispace.backend.catalog.adapter.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogItemJpaRepository
        extends JpaRepository<CatalogItemEntity, UUID> {

    List<CatalogItemEntity> findAllByWorkspaceIdIsNull(Sort sort);

    List<CatalogItemEntity> findAllByWorkspaceId(UUID workspaceId, Sort sort);

    Optional<CatalogItemEntity> findByIdAndWorkspaceId(
            UUID id, UUID workspaceId);
}
