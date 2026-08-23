package com.intellispace.backend.workspace.adapter.in.web;

import com.intellispace.backend.common.security.CurrentUserId;
import com.intellispace.backend.workspace.adapter.in.web.dto.*;
import com.intellispace.backend.workspace.application.port.in.*;
import com.intellispace.backend.workspace.domain.Workspace;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final GetWorkspaceUseCase getWorkspaceUseCase;
    private final ListWorkspacesUseCase listWorkspacesUseCase;
    private final ListFurnitureForWorkspaceUseCase listFurnitureForWorkspaceUseCase;
    private final WorkspaceWebMapper mapper;

    public WorkspaceController(CreateWorkspaceUseCase createWorkspaceUseCase, GetWorkspaceUseCase getWorkspaceUseCase,
                               ListWorkspacesUseCase listWorkspacesUseCase,
                               ListFurnitureForWorkspaceUseCase listFurnitureForWorkspaceUseCase, WorkspaceWebMapper mapper) {
        this.createWorkspaceUseCase = createWorkspaceUseCase;
        this.getWorkspaceUseCase = getWorkspaceUseCase;
        this.listWorkspacesUseCase = listWorkspacesUseCase;
        this.listFurnitureForWorkspaceUseCase = listFurnitureForWorkspaceUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceDetailResponse create(@Valid @RequestBody CreateWorkspaceRequest request, @CurrentUserId UUID ownerId) {
        Workspace created = createWorkspaceUseCase.createWorkspace(mapper.toCommand(request, ownerId));
        return mapper.toDetailResponse(created, List.of());
    }

    @GetMapping
    public List<WorkspaceSummaryResponse> listMine(@CurrentUserId UUID ownerId) {
        return listWorkspacesUseCase.listWorkspacesForOwner(ownerId).stream().map(mapper::toSummaryResponse).toList();
    }

    @GetMapping("/{workspaceId}")
    public WorkspaceDetailResponse getOne(@PathVariable UUID workspaceId) {
        Workspace workspace = getWorkspaceUseCase.getWorkspace(workspaceId);
        var furniture = listFurnitureForWorkspaceUseCase.listFurniture(workspaceId);
        return mapper.toDetailResponse(workspace, furniture);
    }
}