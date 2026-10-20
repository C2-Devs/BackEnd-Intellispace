package com.intellispace.backend.workspace.application.port.out;

import com.intellispace.backend.workspace.domain.WorkspaceArchitecture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceArchitectureRepository {
    WorkspaceArchitecture save(WorkspaceArchitecture architecture);
    Optional<WorkspaceArchitecture> findById(UUID id);
    List<WorkspaceArchitecture> findAllByWorkspaceId(UUID workspaceId);
    void deleteById(UUID id);
}