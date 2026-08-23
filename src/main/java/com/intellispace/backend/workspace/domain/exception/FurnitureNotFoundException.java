package com.intellispace.backend.workspace.domain.exception;

import java.util.UUID;

public class FurnitureNotFoundException extends RuntimeException {
    public FurnitureNotFoundException(UUID furnitureId) {
        super("Furniture not found: " + furnitureId);
    }
}