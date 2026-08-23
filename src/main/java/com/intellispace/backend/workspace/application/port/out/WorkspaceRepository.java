package com.intellispace.backend.workspace.application.port.out;

import com.intellispace.backend.workspace.domain.Workspace;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepository {
    Workspace save(Workspace workspace);
    Optional<Workspace> findById(UUID id);
    List<Workspace> findAllByOwnerId(UUID ownerId);
    void deleteById(UUID id);
}