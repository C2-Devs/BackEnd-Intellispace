package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.Workspace;

import java.util.UUID;

public interface UpdateWorkspaceUseCase {
    Workspace updateWorkspace(UUID workspaceId, UUID requestingUserId, UpdateWorkspaceCommand command);
}
