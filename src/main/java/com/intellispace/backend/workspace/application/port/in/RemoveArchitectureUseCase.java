package com.intellispace.backend.workspace.application.port.in;

import java.util.UUID;

public interface RemoveArchitectureUseCase {
    void removeArchitecture(UUID workspaceId, UUID architectureId, UUID requestingUserId);
}
