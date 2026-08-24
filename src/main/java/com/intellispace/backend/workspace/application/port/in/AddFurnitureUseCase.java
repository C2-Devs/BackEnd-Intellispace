package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.WorkspaceFurniture;

import java.util.UUID;

public interface AddFurnitureUseCase {
    WorkspaceFurniture addFurniture(UUID requestingUserId, AddFurnitureCommand command); // throws WorkspaceNotFoundException if the workspace doesn't exist
}
