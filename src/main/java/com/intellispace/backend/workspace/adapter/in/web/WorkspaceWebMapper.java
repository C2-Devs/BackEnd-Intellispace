package com.intellispace.backend.workspace.adapter.in.web;

import com.intellispace.backend.workspace.adapter.in.web.dto.*;
import com.intellispace.backend.workspace.application.port.in.*;
import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.Record.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WorkspaceWebMapper {

    public CreateWorkspaceCommand toCommand(CreateWorkspaceRequest r, UUID ownerId) {
        RoomGeometry geometry = new RoomGeometry(r.room().width(), r.room().depth(), r.room().height(), r.room().wallThickness());
        RoomAppearance appearance = new RoomAppearance(r.appearance().wallColor(), r.appearance().floorColor(),
                r.appearance().ceilingColor(), r.appearance().lightPreset());
        Money budget = r.budget() != null ? new Money(r.budget(), r.currency()) : null;
        return new CreateWorkspaceCommand(ownerId, r.name(), r.description(), r.roomType(), r.designStyle(), geometry, appearance, budget );
    }
    public UpdateWorkspaceCommand toCommand(UpdateWorkspaceRequest r) {
        RoomAppearance appearance = r.appearance() != null
                ? new RoomAppearance(r.appearance().wallColor(), r.appearance().floorColor(), r.appearance().ceilingColor(), r.appearance().lightPreset())
                : null;
        Money budget = r.budget() != null ? new Money(r.budget(), r.currency()) : null;
        return new UpdateWorkspaceCommand(r.name(), appearance, budget, r.expectedVersion());
    }

    public WorkspaceSummaryResponse toSummaryResponse(Workspace w) {
        return new WorkspaceSummaryResponse(w.getId(), w.getName(), w.getRoomType(), w.getDesignStyle());
    }

    public WorkspaceDetailResponse toDetailResponse(Workspace w, List<WorkspaceFurniture> furniture) {
        var g = w.getGeometry();
        var a = w.getAppearance();
        return new WorkspaceDetailResponse(
                w.getId(), w.getName(), w.getDescription(),
                new WorkspaceDetailResponse.RoomDto(g.width(), g.depth(), g.height(), g.wallThickness()),
                new WorkspaceDetailResponse.AppearanceDto(a.wallColor(), a.floorColor(), a.ceilingColor(), a.lightPreset()),
                w.getBudget().map(Money::amount).orElse(null),
                w.getBudget().map(Money::currency).orElse(null),
                w.getVersion(),
                furniture.stream().map(this::toResponse).toList(),
                List.of()
        );
    }

    public AddFurnitureCommand toCommand(UUID workspaceId, AddFurnitureRequest r) {
        Vector3 position = new Vector3(r.position().x(), r.position().y(), r.position().z());
        Vector3 rotation = r.rotation() != null ? new Vector3(r.rotation().x(), r.rotation().y(), r.rotation().z()) : Vector3.ZERO;
        Scale3 scale = r.scale() != null ? new Scale3(r.scale().x(), r.scale().y(), r.scale().z()) : Scale3.UNIT;
        return new AddFurnitureCommand(workspaceId, r.catalogItemId(), position, rotation, scale);
    }

    public UpdateFurniturePlacementCommand toCommand(UpdateFurniturePlacementRequest r) {
        Vector3 position = r.position() != null ? new Vector3(r.position().x(), r.position().y(), r.position().z()) : null;
        Vector3 rotation = r.rotation() != null ? new Vector3(r.rotation().x(), r.rotation().y(), r.rotation().z()) : null;
        Scale3 scale = r.scale() != null ? new Scale3(r.scale().x(), r.scale().y(), r.scale().z()) : null;
        return new UpdateFurniturePlacementCommand(position, rotation, scale, r.locked(), r.visible(), r.materialOverrides());
    }

    public WorkspaceFurnitureResponse toResponse(WorkspaceFurniture f) {
        return new WorkspaceFurnitureResponse(
                f.getId(), f.getCatalogItemId(),
                new XyzDto(f.getPosition().x(), f.getPosition().y(), f.getPosition().z()),
                new XyzDto(f.getRotation().x(), f.getRotation().y(), f.getRotation().z()),
                new XyzDto(f.getScale().x(), f.getScale().y(), f.getScale().z()),
                f.isLocked(), f.isVisible(), f.getMaterialOverrides()
        );
    }

}