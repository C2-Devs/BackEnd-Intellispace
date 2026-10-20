package com.intellispace.backend.workspace.domain;

import com.intellispace.backend.workspace.domain.Enum.ArchitecturalType;
import com.intellispace.backend.workspace.domain.Enum.WallSide;
import com.intellispace.backend.workspace.domain.Record.OpeningDimensions;
import com.intellispace.backend.workspace.domain.Record.WallOffset;

import java.util.UUID;

public class WorkspaceArchitecture {

    private final UUID id;
    private final UUID workspaceId;
    private final ArchitecturalType elementType;
    private final WallSide wall;
    private WallOffset offset;
    private OpeningDimensions dimensions;

    private WorkspaceArchitecture(UUID id, UUID workspaceId, ArchitecturalType elementType, WallSide wall,
                                  WallOffset offset, OpeningDimensions dimensions) {
        this.id = requireNonNull(id, "id");
        this.workspaceId = requireNonNull(workspaceId, "workspaceId");
        this.elementType = requireNonNull(elementType, "elementType");
        this.wall = requireNonNull(wall, "wall");
        this.offset = requireNonNull(offset, "offset");
        this.dimensions = requireNonNull(dimensions, "dimensions");
    }

    public static WorkspaceArchitecture place(UUID workspaceId, ArchitecturalType elementType, WallSide wall,
                                              WallOffset offset, OpeningDimensions dimensions) {
        return new WorkspaceArchitecture(UUID.randomUUID(), workspaceId, elementType, wall, offset, dimensions);
    }

    public static WorkspaceArchitecture reconstruct(UUID id, UUID workspaceId, ArchitecturalType elementType,
                                                    WallSide wall, WallOffset offset, OpeningDimensions dimensions) {
        return new WorkspaceArchitecture(id, workspaceId, elementType, wall, offset, dimensions);
    }

    public void reposition(WallOffset newOffset) { this.offset = requireNonNull(newOffset, "offset"); }
    public void resize(OpeningDimensions newDimensions) { this.dimensions = requireNonNull(newDimensions, "dimensions"); }

    private static <T> T requireNonNull(T value, String field) {
        if (value == null) throw new IllegalArgumentException(field + " must not be null");
        return value;
    }

    public UUID getId() { return id; }
    public UUID getWorkspaceId() { return workspaceId; }
    public ArchitecturalType getElementType() { return elementType; }
    public WallSide getWall() { return wall; }
    public WallOffset getOffset() { return offset; }
    public OpeningDimensions getDimensions() { return dimensions; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceArchitecture other)) return false;
        return id.equals(other.id);
    }
    @Override
    public int hashCode() { return id.hashCode(); }
}