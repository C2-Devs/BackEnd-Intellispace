package com.intellispace.backend.workspace.application.service;

import com.intellispace.backend.workspace.application.port.in.*;
import com.intellispace.backend.workspace.application.port.out.WorkspaceRepository;
import com.intellispace.backend.workspace.domain.Workspace;
import com.intellispace.backend.workspace.domain.exception.StaleWorkspaceException;
import com.intellispace.backend.workspace.domain.exception.WorkspaceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@Transactional
public class WorkspaceApplicationService implements
        CreateWorkspaceUseCase, GetWorkspaceUseCase, ListWorkspacesUseCase, UpdateWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceApplicationService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public Workspace createWorkspace(CreateWorkspaceCommand command) {
        Workspace workspace = Workspace.create(command.ownerId(), command.name(), command.description(),
                command.roomType(), command.designStyle(), command.geometry(), command.appearance(), command.budget());
        return workspaceRepository.save(workspace);
    }

    @Override
    @Transactional(readOnly = true)
    public Workspace getWorkspace(UUID workspaceId, UUID requestingUserId) {
        return requireOwnedWorkspace(workspaceId, requestingUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Workspace> listWorkspacesForOwner(UUID ownerId) {
        return workspaceRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Workspace updateWorkspace(UUID workspaceId, UUID requestingUserId, UpdateWorkspaceCommand command) {
        Workspace workspace = requireOwnedWorkspace(workspaceId, requestingUserId);
        if (workspace.getVersion() != command.expectedVersion()) {
            throw new StaleWorkspaceException(workspaceId, command.expectedVersion(), workspace.getVersion());
        }
        if (command.name() != null) workspace.rename(command.name());
        if (command.appearance() != null) workspace.updateAppearance(command.appearance());
        if (command.budget() != null) workspace.updateBudget(command.budget());
        // If another request commits a change to this same row between the check above and this
        // save, Hibernate's own @Version mechanism (UPDATE ... WHERE version = ?) still catches
        // it at flush time and throws ObjectOptimisticLockingFailureException — the check above
        // is the fast, specific-message path; this is the actual concurrency guarantee underneath it.
        return workspaceRepository.save(workspace);
    }

    private Workspace requireOwnedWorkspace(UUID workspaceId, UUID requestingUserId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        if (!workspace.getOwnerId().equals(requestingUserId)) {
            throw new WorkspaceNotFoundException(workspaceId); // 404, not 403 — see Step 4's note on not confirming existence to a non-owner
        }
        return workspace;
    }
}