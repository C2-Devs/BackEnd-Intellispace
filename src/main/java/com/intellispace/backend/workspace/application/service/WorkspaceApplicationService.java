package com.intellispace.backend.workspace.application.service;

import com.intellispace.backend.workspace.application.port.in.*;
import com.intellispace.backend.workspace.application.port.out.WorkspaceRepository;
import com.intellispace.backend.workspace.domain.Workspace;
import com.intellispace.backend.workspace.domain.exception.WorkspaceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WorkspaceApplicationService implements CreateWorkspaceUseCase, GetWorkspaceUseCase, ListWorkspacesUseCase {

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
    public Workspace getWorkspace(UUID workspaceId) {
        return workspaceRepository.findById(workspaceId).orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Workspace> listWorkspacesForOwner(UUID ownerId) {
        return workspaceRepository.findAllByOwnerId(ownerId);
    }
}