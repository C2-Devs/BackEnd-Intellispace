package com.intellispace.backend.workspace.domain.Record;

/** Where on a flat wall an opening sits — the 2D analog of what Vector3 does for full 3D furniture position. */
public record WallOffset(double alongWall, double fromFloor) {
    public WallOffset {
        if (!Double.isFinite(alongWall) || !Double.isFinite(fromFloor)) {
            throw new IllegalArgumentException("Offset must be finite");
        }
        if (alongWall < 0 || fromFloor < 0) {
            throw new IllegalArgumentException("Offset must be non-negative: (%s, %s)".formatted(alongWall, fromFloor));
        }
    }
}