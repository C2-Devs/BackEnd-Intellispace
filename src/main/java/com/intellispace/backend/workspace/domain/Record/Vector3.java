package com.intellispace.backend.workspace.domain.Record;

public record Vector3(double x, double y, double z) {

    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    public Vector3 {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException(
                    "Vector3 components must be finite: (%s, %s, %s)".formatted(x, y, z));
        }

    }

}
