package com.intellispace.backend.workspace.domain;

import com.intellispace.backend.workspace.domain.Record.Scale3;
import com.intellispace.backend.workspace.domain.Record.Vector3;
import com.intellispace.backend.workspace.domain.exception.FurnitureLockedException;

import java.util.Map;
import java.util.UUID;

public class WorkspaceFurniture {

    private final UUID id;
    private final UUID workspaceId;
    private final UUID catalogItemId;
    private Vector3 position;
    private Vector3 rotation;
    private Scale3 scale;
    private boolean locked;
    private boolean visible;
    private Map<String, Object> materialOverrides;

    private WorkspaceFurniture(UUID id, UUID workspaceId, UUID catalogItemId, Vector3 position, Vector3 rotation,
                               Scale3 scale, boolean locked, boolean visible, Map<String, Object> materialOverrides) {
        this.id = requireNonNull(id, "id");
        this.workspaceId = requireNonNull(workspaceId, "workspaceId");
        this.catalogItemId = requireNonNull(catalogItemId, "catalogItemId");
        this.position = requireNonNull(position, "position");
        this.rotation = requireNonNull(rotation, "rotation");
        this.scale = requireNonNull(scale, "scale");
        this.locked = locked;
        this.visible = visible;
        this.materialOverrides = materialOverrides;
    }

    public static WorkspaceFurniture place(UUID workspaceId, UUID catalogItemId, Vector3 position, Vector3 rotation, Scale3 scale) {
        return new WorkspaceFurniture(UUID.randomUUID(), workspaceId, catalogItemId, position, rotation, scale, false, true, null);
    }

    public static WorkspaceFurniture reconstruct(UUID id, UUID workspaceId, UUID catalogItemId, Vector3 position,
                                                 Vector3 rotation, Scale3 scale, boolean locked, boolean visible,
                                                 Map<String, Object> materialOverrides) {
        return new WorkspaceFurniture(id, workspaceId, catalogItemId, position, rotation, scale, locked, visible, materialOverrides);
    }

    public void moveTo(Vector3 newPosition) {
        requireUnlocked();
        this.position = requireNonNull(newPosition, "position");
    }

    public void rotateTo(Vector3 newRotation) {
        requireUnlocked();
        this.rotation = requireNonNull(newRotation, "rotation");
    }

    public void scaleTo(Scale3 newScale) {
        requireUnlocked();
        this.scale = requireNonNull(newScale, "scale");
    }

    public void lock()   { this.locked = true; }
    public void unlock() { this.locked = false; }
    public void show()   { this.visible = true; }
    public void hide()   { this.visible = false; }

    public void updateMaterialOverrides(Map<String, Object> overrides) {
        this.materialOverrides = overrides;
    }

    private void requireUnlocked() {
        if (locked) {
            throw new FurnitureLockedException(id);
        }
    }

    private static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return value;
    }

    public UUID getId() { return id; }
    public UUID getWorkspaceId() { return workspaceId; }
    public UUID getCatalogItemId() { return catalogItemId; }
    public Vector3 getPosition() { return position; }
    public Vector3 getRotation() { return rotation; }
    public Scale3 getScale() { return scale; }
    public boolean isLocked() { return locked; }
    public boolean isVisible() { return visible; }
    public Map<String, Object> getMaterialOverrides() { return materialOverrides; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkspaceFurniture other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}