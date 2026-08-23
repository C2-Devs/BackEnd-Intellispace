package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.WorkspaceFurniture;

import java.util.UUID;

public interface UpdateFurniturePlacementUseCase {
    WorkspaceFurniture updateFurniturePlacement(UUID furnitureId, UpdateFurniturePlacementCommand command);
}
