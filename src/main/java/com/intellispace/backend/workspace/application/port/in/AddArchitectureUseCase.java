package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.WorkspaceArchitecture;

import java.util.UUID;

public interface AddArchitectureUseCase {
    WorkspaceArchitecture addArchitecture(UUID requestingUserId, AddArchitectureCommand command);
}
