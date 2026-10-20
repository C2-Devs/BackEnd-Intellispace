package com.intellispace.backend.workspace.domain.Record;

public record OpeningDimensions(double width, double height) {
    public OpeningDimensions {
        if (!Double.isFinite(width) || !Double.isFinite(height)) {
            throw new IllegalArgumentException("Dimensions must be finite");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive: (%s, %s)".formatted(width, height));
        }
    }
}