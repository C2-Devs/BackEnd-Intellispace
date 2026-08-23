package com.intellispace.backend.workspace.domain.exception;

import java.util.UUID;

public class FurnitureLockedException extends RuntimeException {
    public FurnitureLockedException(UUID furnitureId) {
        super("Cannot transform furniture " + furnitureId + " while it is locked");
    }
}
