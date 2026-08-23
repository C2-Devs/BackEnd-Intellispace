package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.WorkspaceFurniture;

public interface AddFurnitureUseCase {
    WorkspaceFurniture addFurniture(AddFurnitureCommand command); // throws WorkspaceNotFoundException if the workspace doesn't exist
}
