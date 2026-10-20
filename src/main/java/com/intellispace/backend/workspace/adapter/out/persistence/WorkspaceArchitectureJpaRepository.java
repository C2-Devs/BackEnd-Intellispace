package com.intellispace.backend.workspace.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

interface WorkspaceArchitectureJpaRepository extends JpaRepository<WorkspaceArchitectureEntity, UUID> {
    List<WorkspaceArchitectureEntity> findAllByWorkspaceId(UUID workspaceId); // the bare version from Step 6 gains this
}