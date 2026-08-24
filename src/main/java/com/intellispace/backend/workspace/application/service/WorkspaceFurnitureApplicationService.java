package com.intellispace.backend.workspace.application.service;

import com.intellispace.backend.workspace.application.port.in.*;
import com.intellispace.backend.workspace.application.port.out.*;
import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.exception.WorkspaceNotFoundException;
import com.intellispace.backend.workspace.domain.exception.FurnitureNotFoundException;
import com.intellispace.backend.workspace.domain.exception.CatalogItemNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WorkspaceFurnitureApplicationService implements
        AddFurnitureUseCase, UpdateFurniturePlacementUseCase, RemoveFurnitureUseCase, ListFurnitureForWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceFurnitureRepository furnitureRepository;
    private final CatalogItemLookupPort catalogItemLookupPort;

    public WorkspaceFurnitureApplicationService(WorkspaceRepository workspaceRepository,
                                                WorkspaceFurnitureRepository furnitureRepository,
                                                CatalogItemLookupPort catalogItemLookupPort) {
        this.workspaceRepository = workspaceRepository;
        this.furnitureRepository = furnitureRepository;
        this.catalogItemLookupPort = catalogItemLookupPort;
    }

    @Override
    public WorkspaceFurniture addFurniture(UUID requestingUserId, AddFurnitureCommand command) {
        requireOwnedWorkspace(command.workspaceId(), requestingUserId);
        if (!catalogItemLookupPort.exists(command.catalogItemId())) {
            throw new CatalogItemNotFoundException(command.catalogItemId());
        }
        WorkspaceFurniture furniture = WorkspaceFurniture.place(
                command.workspaceId(), command.catalogItemId(), command.position(), command.rotation(), command.scale());
        return furnitureRepository.save(furniture);
    }

    @Override
    public WorkspaceFurniture updateFurniturePlacement(UUID requestingUserId, UUID workspaceId, UUID furnitureId,
                                                       UpdateFurniturePlacementCommand command) {
        requireOwnedWorkspace(workspaceId, requestingUserId);
        WorkspaceFurniture furniture = requireFurnitureInWorkspace(furnitureId, workspaceId);

        if (command.position() != null) furniture.moveTo(command.position());
        if (command.rotation() != null) furniture.rotateTo(command.rotation());
        if (command.scale() != null) furniture.scaleTo(command.scale());
        if (command.locked() != null) { if (command.locked()) furniture.lock(); else furniture.unlock(); }
        if (command.visible() != null) { if (command.visible()) furniture.show(); else furniture.hide(); }
        if (command.materialOverrides() != null) furniture.updateMaterialOverrides(command.materialOverrides());

        return furnitureRepository.save(furniture);
    }

    @Override
    public void removeFurniture(UUID workspaceId, UUID furnitureId, UUID requestingUserId) {
        requireOwnedWorkspace(workspaceId, requestingUserId);
        requireFurnitureInWorkspace(furnitureId, workspaceId);
        furnitureRepository.deleteById(furnitureId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceFurniture> listFurniture(UUID workspaceId, UUID requestingUserId) {
        requireOwnedWorkspace(workspaceId, requestingUserId);
        return furnitureRepository.findAllByWorkspaceId(workspaceId);
    }

    private Workspace requireOwnedWorkspace(UUID workspaceId, UUID requestingUserId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        if (!workspace.getOwnerId().equals(requestingUserId)) {
            throw new WorkspaceNotFoundException(workspaceId);
        }
        return workspace;
    }

    private WorkspaceFurniture requireFurnitureInWorkspace(UUID furnitureId, UUID workspaceId) {
        WorkspaceFurniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new FurnitureNotFoundException(furnitureId));
        if (!furniture.getWorkspaceId().equals(workspaceId)) {
            // Treat "wrong workspace" identically to "doesn't exist" — same reasoning as ownership above.
            throw new FurnitureNotFoundException(furnitureId);
        }
        return furniture;
    }
}