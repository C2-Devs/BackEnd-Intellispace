package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.WorkspaceFurniture;

import java.util.UUID;

public interface ListFurnitureForWorkspaceUseCase {
    java.util.List<WorkspaceFurniture> listFurniture(UUID requestingUserId,UUID workspaceId);
}
