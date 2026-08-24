package com.intellispace.backend.workspace.adapter.out.persistence;

import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.Record.Scale3;
import com.intellispace.backend.workspace.domain.Record.Vector3;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceFurnitureMapper {

    public WorkspaceFurnitureEntity toNewEntity(WorkspaceFurniture f) {
        WorkspaceFurnitureEntity entity = WorkspaceFurnitureEntity.builder()
                .workspaceId(f.getWorkspaceId()).catalogItemId(f.getCatalogItemId())
                .posX(f.getPosition().x()).posY(f.getPosition().y()).posZ(f.getPosition().z())
                .rotX(f.getRotation().x()).rotY(f.getRotation().y()).rotZ(f.getRotation().z())
                .scaleX(f.getScale().x()).scaleY(f.getScale().y()).scaleZ(f.getScale().z())
                .locked(f.isLocked()).visible(f.isVisible()).materialOverrides(f.getMaterialOverrides())
                .build();
        entity.setId(f.getId()); // id is managed by the domain, not Hibernate
        return entity;
    }

    public WorkspaceFurnitureEntity updateEntity(WorkspaceFurnitureEntity existing, WorkspaceFurniture f) {
        existing.setPosX(f.getPosition().x()); existing.setPosY(f.getPosition().y()); existing.setPosZ(f.getPosition().z());
        existing.setRotX(f.getRotation().x()); existing.setRotY(f.getRotation().y()); existing.setRotZ(f.getRotation().z());
        existing.setScaleX(f.getScale().x()); existing.setScaleY(f.getScale().y()); existing.setScaleZ(f.getScale().z());
        existing.setLocked(f.isLocked()); existing.setVisible(f.isVisible());
        existing.setMaterialOverrides(f.getMaterialOverrides());
        return existing;
    }

    public WorkspaceFurniture toDomain(WorkspaceFurnitureEntity e) {
        return WorkspaceFurniture.reconstruct(e.getId(), e.getWorkspaceId(), e.getCatalogItemId(),
                new Vector3(e.getPosX(), e.getPosY(), e.getPosZ()),
                new Vector3(e.getRotX(), e.getRotY(), e.getRotZ()),
                new Scale3(e.getScaleX(), e.getScaleY(), e.getScaleZ()),
                e.isLocked(), e.isVisible(), e.getMaterialOverrides());
    }
}