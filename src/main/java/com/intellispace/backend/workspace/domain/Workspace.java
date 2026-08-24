package com.intellispace.backend.workspace.domain;

import com.intellispace.backend.workspace.domain.Record.Money;
import com.intellispace.backend.workspace.domain.Record.RoomAppearance;
import com.intellispace.backend.workspace.domain.Record.RoomGeometry;

import java.util.UUID;
import java.util.Optional;

public class Workspace {

    private final UUID id;
    private final UUID ownerId;
    private String name;
    private String description;
    private String roomType;
    private String designStyle;
    private RoomGeometry geometry;
    private RoomAppearance appearance;
    private Money budget;
    private final int version;

    private Workspace(UUID id, UUID ownerId, String name, String description, String roomType,
                      String designStyle, RoomGeometry geometry, RoomAppearance appearance, Money budget, int version) {
        this.id = requireNonNull(id, "id");
        this.ownerId = requireNonNull(ownerId, "ownerId");
        this.name = requireName(name);
        this.description = description;
        this.roomType = roomType;
        this.designStyle = designStyle;
        this.geometry = requireNonNull(geometry, "geometry");
        this.appearance = requireNonNull(appearance, "appearance");
        this.budget = budget;
        this.version = version;
    }

    /** A brand-new workspace. Assigns its own identity — see the note below. */
    public static Workspace create(UUID ownerId, String name, String description, String roomType,
                                   String designStyle, RoomGeometry geometry, RoomAppearance appearance, Money budget) {
        return new Workspace(UUID.randomUUID(), ownerId, name, description, roomType, designStyle, geometry, appearance, budget,0);
    }

    /** Rehydrates a workspace persistence already knows about. Called only by the persistence mapper (Step 4). */
    public static Workspace reconstruct(UUID id, UUID ownerId, String name, String description, String roomType,
                                        String designStyle, RoomGeometry geometry, RoomAppearance appearance, Money budget, int version) {
        return new Workspace(id, ownerId, name, description, roomType, designStyle, geometry, appearance, budget,version);
    }

    public int getVersion() { return version; }

    public void rename(String newName) {
        this.name = requireName(newName);
    }

    public void updateAppearance(RoomAppearance newAppearance) {
        this.appearance = requireNonNull(newAppearance, "appearance");
    }

    public void updateBudget(Money newBudget) {
        this.budget = newBudget; // null is valid: it means "no budget set"
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Workspace name must not be blank");
        }
        if (name.length() > 200) {
            throw new IllegalArgumentException("Workspace name must not exceed 200 characters");
        }
        return name;
    }

    private static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return value;
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRoomType() { return roomType; }
    public String getDesignStyle() { return designStyle; }
    public RoomGeometry getGeometry() { return geometry; }
    public RoomAppearance getAppearance() { return appearance; }
    public Optional<Money> getBudget() { return Optional.ofNullable(budget); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workspace other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}