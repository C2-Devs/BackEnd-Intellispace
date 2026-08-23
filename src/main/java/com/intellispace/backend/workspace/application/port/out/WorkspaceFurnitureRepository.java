package com.intellispace.backend.workspace.application.port.out;

import com.intellispace.backend.workspace.domain.WorkspaceFurniture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceFurnitureRepository {
    WorkspaceFurniture save(WorkspaceFurniture furniture);
    Optional<WorkspaceFurniture> findById(UUID id);
    List<WorkspaceFurniture> findAllByWorkspaceId(UUID workspaceId);
    void deleteById(UUID id);
}