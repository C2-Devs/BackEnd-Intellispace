package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.WorkspaceArchitecture;

import java.util.List;
import java.util.UUID;

public interface ListArchitectureForWorkspaceUseCase {
    List<WorkspaceArchitecture> listArchitecture(UUID workspaceId, UUID requestingUserId);
}
