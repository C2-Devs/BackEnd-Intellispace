package com.intellispace.backend.workspace.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

interface WorkspaceFurnitureJpaRepository extends JpaRepository<WorkspaceFurnitureEntity, UUID> {
    List<WorkspaceFurnitureEntity> findAllByWorkspaceId(UUID workspaceId);
}