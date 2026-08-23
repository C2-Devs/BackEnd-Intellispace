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
    public WorkspaceFurniture addFurniture(AddFurnitureCommand command) {
        workspaceRepository.findById(command.workspaceId())
                .orElseThrow(() -> new WorkspaceNotFoundException(command.workspaceId()));
        if (!catalogItemLookupPort.exists(command.catalogItemId())) {
            throw new CatalogItemNotFoundException(command.catalogItemId());
        }
        WorkspaceFurniture furniture = WorkspaceFurniture.place(
                command.workspaceId(), command.catalogItemId(), command.position(), command.rotation(), command.scale());
        return furnitureRepository.save(furniture);
    }

    @Override
    public WorkspaceFurniture updateFurniturePlacement(UUID furnitureId, UpdateFurniturePlacementCommand command) {
        WorkspaceFurniture furniture = furnitureRepository.findById(furnitureId)
                .orElseThrow(() -> new FurnitureNotFoundException(furnitureId));

        // Transforms first, lock state second — deliberately. A single PATCH can legally carry
        // both a final position AND locked:true ("move it here, then lock it"). Reversing this
        // order would make that request fail: moveTo() rejects a furniture that's already locked.
        if (command.position() != null) furniture.moveTo(command.position());
        if (command.rotation() != null) furniture.rotateTo(command.rotation());
        if (command.scale() != null) furniture.scaleTo(command.scale());
        if (command.locked() != null) {
            if (command.locked()) furniture.lock(); else furniture.unlock();
        }
        if (command.visible() != null) {
            if (command.visible()) furniture.show(); else furniture.hide();
        }
        if (command.materialOverrides() != null) furniture.updateMaterialOverrides(command.materialOverrides());

        return furnitureRepository.save(furniture);
    }

    @Override
    public void removeFurniture(UUID furnitureId) {
        // Checked explicitly rather than calling deleteById() straight away: Spring Data's
        // deleteById() throws EmptyResultDataAccessException on a missing row, which would leak
        // a Spring-internal exception type up through this port instead of our own domain exception.
        if (furnitureRepository.findById(furnitureId).isEmpty()) {
            throw new FurnitureNotFoundException(furnitureId);
        }
        furnitureRepository.deleteById(furnitureId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceFurniture> listFurniture(UUID workspaceId) {
        return furnitureRepository.findAllByWorkspaceId(workspaceId);
    }
}