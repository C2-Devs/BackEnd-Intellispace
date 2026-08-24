package com.intellispace.backend.workspace.domain.exception;

import java.util.UUID;

public class StaleWorkspaceException extends RuntimeException {
    public StaleWorkspaceException(UUID workspaceId, int expectedVersion, int actualVersion) {
        super("Workspace %s was modified since you last read it (you had version %d, current is %d)"
                .formatted(workspaceId, expectedVersion, actualVersion));
    }
}