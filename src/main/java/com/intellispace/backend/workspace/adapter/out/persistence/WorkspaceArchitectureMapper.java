package com.intellispace.backend.workspace.adapter.out.persistence;

import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.Record.OpeningDimensions;
import com.intellispace.backend.workspace.domain.Record.WallOffset;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceArchitectureMapper {

    public WorkspaceArchitectureEntity toNewEntity(WorkspaceArchitecture a) {
        WorkspaceArchitectureEntity entity = WorkspaceArchitectureEntity.builder()
                .workspaceId(a.getWorkspaceId())
                .elementType(a.getElementType()).wall(a.getWall())
                .wallPosition(a.getOffset().alongWall()).sillHeight(a.getOffset().fromFloor())
                .width(a.getDimensions().width()).height(a.getDimensions().height())
                .build();
        entity.setId(a.getId());
        return entity;
    }

    public WorkspaceArchitectureEntity updateEntity(WorkspaceArchitectureEntity existing, WorkspaceArchitecture a) {
        existing.setWallPosition(a.getOffset().alongWall());
        existing.setSillHeight(a.getOffset().fromFloor());
        existing.setWidth(a.getDimensions().width());
        existing.setHeight(a.getDimensions().height());
        return existing;
    }

    public WorkspaceArchitecture toDomain(WorkspaceArchitectureEntity e) {
        return WorkspaceArchitecture.reconstruct(e.getId(), e.getWorkspaceId(), e.getElementType(), e.getWall(),
                new WallOffset(e.getWallPosition(), e.getSillHeight()),
                new OpeningDimensions(e.getWidth(), e.getHeight()));
    }
}