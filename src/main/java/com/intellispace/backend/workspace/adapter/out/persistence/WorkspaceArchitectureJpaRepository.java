package com.intellispace.backend.workspace.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

interface WorkspaceArchitectureJpaRepository extends JpaRepository<WorkspaceArchitectureEntity, UUID> {
}