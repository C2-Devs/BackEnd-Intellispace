package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.Workspace;
import java.util.List;
import java.util.UUID;

public interface GetWorkspaceUseCase {
    Workspace getWorkspace(UUID workspaceId); // throws WorkspaceNotFoundException
}

