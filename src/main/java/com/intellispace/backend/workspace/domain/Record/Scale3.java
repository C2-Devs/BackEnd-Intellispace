package com.intellispace.backend.workspace.domain.Record;

public record Scale3(double x, double y, double z) {

    public static final Scale3 UNIT = new Scale3(1, 1, 1);

    public Scale3 {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException(
                    "Scale3 components must be finite: (%s, %s, %s)".formatted(x, y, z));
        }
        if (x <= 0 || y <= 0 || z <= 0) {
            throw new IllegalArgumentException(
                    "Scale3 components must be positive: (%s, %s, %s)".formatted(x, y, z));
        }
    }
}