package com.intellispace.backend.workspace.adapter.in.web;

import com.intellispace.backend.common.security.CurrentUserId;
import com.intellispace.backend.workspace.adapter.in.web.dto.*;
import com.intellispace.backend.workspace.application.port.in.*;
import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.Record.OpeningDimensions;
import com.intellispace.backend.workspace.domain.Record.WallOffset;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/architecture")
@Tag(name = "Architecture", description = "Doors and windows belonging to a workspace")
public class WorkspaceArchitectureController {

    private final AddArchitectureUseCase addArchitectureUseCase;
    private final UpdateArchitectureUseCase updateArchitectureUseCase;
    private final RemoveArchitectureUseCase removeArchitectureUseCase;
    private final ListArchitectureForWorkspaceUseCase listArchitectureForWorkspaceUseCase;

    public WorkspaceArchitectureController(AddArchitectureUseCase addArchitectureUseCase, UpdateArchitectureUseCase updateArchitectureUseCase,
                                           RemoveArchitectureUseCase removeArchitectureUseCase, ListArchitectureForWorkspaceUseCase listArchitectureForWorkspaceUseCase) {
        this.addArchitectureUseCase = addArchitectureUseCase;
        this.updateArchitectureUseCase = updateArchitectureUseCase;
        this.removeArchitectureUseCase = removeArchitectureUseCase;
        this.listArchitectureForWorkspaceUseCase = listArchitectureForWorkspaceUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceArchitectureResponse add(@PathVariable UUID workspaceId, @CurrentUserId UUID userId, @Valid @RequestBody AddArchitectureRequest request) {
        var command = new AddArchitectureCommand(workspaceId, request.elementType(), request.wall(),
                new WallOffset(request.alongWall(), request.fromFloor()), new OpeningDimensions(request.width(), request.height()));
        return toResponse(addArchitectureUseCase.addArchitecture(userId, command));
    }

    @GetMapping
    public List<WorkspaceArchitectureResponse> list(@PathVariable UUID workspaceId, @CurrentUserId UUID userId) {
        return listArchitectureForWorkspaceUseCase.listArchitecture(workspaceId, userId).stream().map(this::toResponse).toList();
    }

    @PatchMapping("/{architectureId}")
    public WorkspaceArchitectureResponse update(@PathVariable UUID workspaceId, @PathVariable UUID architectureId,
                                                @CurrentUserId UUID userId, @Valid @RequestBody UpdateArchitectureRequest request) {
        WallOffset offset = request.offset() != null ? new WallOffset(request.offset().alongWall(), request.offset().fromFloor()) : null;
        OpeningDimensions dims = request.dimensions() != null ? new OpeningDimensions(request.dimensions().width(), request.dimensions().height()) : null;
        var updated = updateArchitectureUseCase.updateArchitecture(workspaceId, architectureId, userId, new UpdateArchitectureCommand(offset, dims));
        return toResponse(updated);
    }

    @DeleteMapping("/{architectureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable UUID workspaceId, @PathVariable UUID architectureId, @CurrentUserId UUID userId) {
        removeArchitectureUseCase.removeArchitecture(workspaceId, architectureId, userId);
    }

    private WorkspaceArchitectureResponse toResponse(WorkspaceArchitecture a) {
        return new WorkspaceArchitectureResponse(a.getId(), a.getElementType(), a.getWall(),
                a.getOffset().alongWall(), a.getOffset().fromFloor(), a.getDimensions().width(), a.getDimensions().height());
    }
}