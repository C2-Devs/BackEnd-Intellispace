package com.intellispace.backend.workspace.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

interface WorkspaceJpaRepository extends JpaRepository<WorkspaceEntity, UUID> {
    List<WorkspaceEntity> findAllByUserId(UUID userId);
}