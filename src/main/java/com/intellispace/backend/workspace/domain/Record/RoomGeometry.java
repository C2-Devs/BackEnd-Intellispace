package com.intellispace.backend.workspace.domain.Record;

public record RoomGeometry(double width, double depth, double height, double wallThickness) {

    public RoomGeometry {
        if (!Double.isFinite(width) || !Double.isFinite(depth) || !Double.isFinite(height) || !Double.isFinite(wallThickness)) {
            throw new IllegalArgumentException("Room geometry values must be finite");
        }
        if (width <= 0 || depth <= 0 || height <= 0 || wallThickness <= 0) {
            throw new IllegalArgumentException("Room geometry values must be positive");
        }
    }
}