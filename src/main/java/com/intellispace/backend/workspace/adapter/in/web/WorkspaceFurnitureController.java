package com.intellispace.backend.workspace.adapter.in.web;

import com.intellispace.backend.workspace.adapter.in.web.dto.*;
import com.intellispace.backend.workspace.application.port.in.*;
import com.intellispace.backend.workspace.domain.WorkspaceFurniture;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/furniture")
public class WorkspaceFurnitureController {

    private final AddFurnitureUseCase addFurnitureUseCase;
    private final UpdateFurniturePlacementUseCase updateFurniturePlacementUseCase;
    private final RemoveFurnitureUseCase removeFurnitureUseCase;
    private final WorkspaceWebMapper mapper;

    public WorkspaceFurnitureController(AddFurnitureUseCase addFurnitureUseCase,
                                        UpdateFurniturePlacementUseCase updateFurniturePlacementUseCase,
                                        RemoveFurnitureUseCase removeFurnitureUseCase, WorkspaceWebMapper mapper) {
        this.addFurnitureUseCase = addFurnitureUseCase;
        this.updateFurniturePlacementUseCase = updateFurniturePlacementUseCase;
        this.removeFurnitureUseCase = removeFurnitureUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceFurnitureResponse add(@PathVariable UUID workspaceId, @Valid @RequestBody AddFurnitureRequest request) {
        WorkspaceFurniture furniture = addFurnitureUseCase.addFurniture(mapper.toCommand(workspaceId, request));
        return mapper.toResponse(furniture);
    }

    @PatchMapping("/{furnitureId}")
    public WorkspaceFurnitureResponse update(@PathVariable UUID workspaceId, @PathVariable UUID furnitureId,
                                             @Valid @RequestBody UpdateFurniturePlacementRequest request) {
        WorkspaceFurniture furniture = updateFurniturePlacementUseCase.updateFurniturePlacement(furnitureId, mapper.toCommand(request));
        return mapper.toResponse(furniture);
    }

    @DeleteMapping("/{furnitureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable UUID workspaceId, @PathVariable UUID furnitureId) {
        removeFurnitureUseCase.removeFurniture(furnitureId);
    }
}