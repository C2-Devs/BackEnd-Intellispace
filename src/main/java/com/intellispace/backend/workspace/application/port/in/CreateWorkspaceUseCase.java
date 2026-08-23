package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.Workspace;

public interface CreateWorkspaceUseCase {
    Workspace createWorkspace(CreateWorkspaceCommand command);
}
