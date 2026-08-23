package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.Workspace;

import java.util.List;
import java.util.UUID;

public interface ListWorkspacesUseCase {
    List<Workspace> listWorkspacesForOwner(UUID ownerId);
}
