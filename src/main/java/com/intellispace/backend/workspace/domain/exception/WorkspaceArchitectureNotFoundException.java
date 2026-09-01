package com.intellispace.backend.workspace.domain.exception;

import java.util.UUID;

public class WorkspaceArchitectureNotFoundException extends RuntimeException {
    public WorkspaceArchitectureNotFoundException(UUID id) { super("Architectural element not found: " + id); }
}