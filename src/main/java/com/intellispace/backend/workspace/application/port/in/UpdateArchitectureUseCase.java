package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.WorkspaceArchitecture;

import java.util.UUID;

public interface UpdateArchitectureUseCase {
    WorkspaceArchitecture updateArchitecture(UUID workspaceId, UUID architectureId, UUID requestingUserId, UpdateArchitectureCommand command);
}
