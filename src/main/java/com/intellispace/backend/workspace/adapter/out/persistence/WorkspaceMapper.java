package com.intellispace.backend.workspace.adapter.out.persistence;


import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.Record.Money;
import com.intellispace.backend.workspace.domain.Record.RoomAppearance;
import com.intellispace.backend.workspace.domain.Record.RoomGeometry;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMapper {

    public WorkspaceEntity toNewEntity(Workspace workspace) {
        WorkspaceEntity entity = WorkspaceEntity.builder()
                .userId(workspace.getOwnerId())
                .name(workspace.getName())
                .description(workspace.getDescription())
                .roomType(workspace.getRoomType())
                .designStyle(workspace.getDesignStyle())
                .roomWidth(workspace.getGeometry().width())
                .roomDepth(workspace.getGeometry().depth())
                .roomHeight(workspace.getGeometry().height())
                .wallThickness(workspace.getGeometry().wallThickness())
                .wallColor(workspace.getAppearance().wallColor())
                .floorColor(workspace.getAppearance().floorColor())
                .ceilingColor(workspace.getAppearance().ceilingColor())
                .lightPreset(workspace.getAppearance().lightPreset())
                .budget(workspace.getBudget().map(Money::amount).orElse(null))
                .currency(workspace.getBudget().map(Money::currency).orElse("INR"))
                .build();
        entity.setId(workspace.getId()); // id is managed by the domain, not Hibernate
        return entity;
    }

    public WorkspaceEntity updateEntity(WorkspaceEntity existing, Workspace workspace) {
        existing.setName(workspace.getName());
        existing.setDescription(workspace.getDescription());
        existing.setRoomType(workspace.getRoomType());
        existing.setDesignStyle(workspace.getDesignStyle());
        existing.setRoomWidth(workspace.getGeometry().width());
        existing.setRoomDepth(workspace.getGeometry().depth());
        existing.setRoomHeight(workspace.getGeometry().height());
        existing.setWallThickness(workspace.getGeometry().wallThickness());
        existing.setWallColor(workspace.getAppearance().wallColor());
        existing.setFloorColor(workspace.getAppearance().floorColor());
        existing.setCeilingColor(workspace.getAppearance().ceilingColor());
        existing.setLightPreset(workspace.getAppearance().lightPreset());
        existing.setBudget(workspace.getBudget().map(Money::amount).orElse(null));
        existing.setCurrency(workspace.getBudget().map(Money::currency).orElse(existing.getCurrency()));
        return existing; // id, version, createdAt, updatedAt: never touched — Hibernate still owns these
    }

    public Workspace toDomain(WorkspaceEntity entity) {
        RoomGeometry geometry = new RoomGeometry(entity.getRoomWidth(), entity.getRoomDepth(),
                entity.getRoomHeight(), entity.getWallThickness());
        RoomAppearance appearance = new RoomAppearance(entity.getWallColor(), entity.getFloorColor(),
                entity.getCeilingColor(), entity.getLightPreset());
        Money budget = entity.getBudget() != null ? new Money(entity.getBudget(), entity.getCurrency()) : null;
        return Workspace.reconstruct(entity.getId(), entity.getUserId(), entity.getName(), entity.getDescription(),
                entity.getRoomType(), entity.getDesignStyle(), geometry, appearance, budget, entity.getVersion());
    }

}