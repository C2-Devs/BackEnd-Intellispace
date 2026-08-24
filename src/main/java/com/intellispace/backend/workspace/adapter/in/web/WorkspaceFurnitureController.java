package com.intellispace.backend.workspace.adapter.in.web;

import com.intellispace.backend.common.security.CurrentUserId;
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
    public WorkspaceFurnitureResponse add(@PathVariable UUID workspaceId,
                                          @CurrentUserId UUID userId,
                                          @Valid @RequestBody AddFurnitureRequest request) {
        WorkspaceFurniture furniture = addFurnitureUseCase.addFurniture(userId, mapper.toCommand(workspaceId, request));
        return mapper.toResponse(furniture);
    }

    // Bug 4 fixed: workspaceId was missing; arg order corrected to match interface (userId, workspaceId, furnitureId, command)
    @PatchMapping("/{furnitureId}")
    public WorkspaceFurnitureResponse update(@PathVariable UUID workspaceId,
                                             @PathVariable UUID furnitureId,
                                             @CurrentUserId UUID userId,
                                             @Valid @RequestBody UpdateFurniturePlacementRequest request) {
        WorkspaceFurniture furniture = updateFurniturePlacementUseCase
                .updateFurniturePlacement(userId, workspaceId, furnitureId, mapper.toCommand(request));
        return mapper.toResponse(furniture);
    }

    // Bug 5 fixed: arg order was (furnitureId, userId, workspaceId) — now correctly (workspaceId, furnitureId, userId)
    @DeleteMapping("/{furnitureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable UUID workspaceId,
                       @CurrentUserId UUID userId,
                       @PathVariable UUID furnitureId) {
        removeFurnitureUseCase.removeFurniture(workspaceId, furnitureId, userId);
    }
}