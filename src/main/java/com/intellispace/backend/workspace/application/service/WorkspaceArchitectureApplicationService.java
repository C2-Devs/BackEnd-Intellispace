package com.intellispace.backend.workspace.application.service;

import com.intellispace.backend.workspace.application.port.in.*;
import com.intellispace.backend.workspace.application.port.out.*;
import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.exception.WorkspaceArchitectureNotFoundException;
import com.intellispace.backend.workspace.domain.exception.WorkspaceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WorkspaceArchitectureApplicationService implements
        AddArchitectureUseCase, UpdateArchitectureUseCase, RemoveArchitectureUseCase, ListArchitectureForWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceArchitectureRepository architectureRepository;

    public WorkspaceArchitectureApplicationService(WorkspaceRepository workspaceRepository, WorkspaceArchitectureRepository architectureRepository) {
        this.workspaceRepository = workspaceRepository;
        this.architectureRepository = architectureRepository;
    }

    @Override
    public WorkspaceArchitecture addArchitecture(UUID requestingUserId, AddArchitectureCommand command) {
        requireOwnedWorkspace(command.workspaceId(), requestingUserId);
        var architecture = WorkspaceArchitecture.place(command.workspaceId(), command.elementType(), command.wall(), command.offset(), command.dimensions());
        return architectureRepository.save(architecture);
    }

    @Override
    public WorkspaceArchitecture updateArchitecture(UUID workspaceId, UUID architectureId, UUID requestingUserId, UpdateArchitectureCommand command) {
        requireOwnedWorkspace(workspaceId, requestingUserId);
        WorkspaceArchitecture architecture = requireArchitectureInWorkspace(architectureId, workspaceId);
        if (command.offset() != null) architecture.reposition(command.offset());
        if (command.dimensions() != null) architecture.resize(command.dimensions());
        return architectureRepository.save(architecture);
    }

    @Override
    public void removeArchitecture(UUID workspaceId, UUID architectureId, UUID requestingUserId) {
        requireOwnedWorkspace(workspaceId, requestingUserId);
        requireArchitectureInWorkspace(architectureId, workspaceId);
        architectureRepository.deleteById(architectureId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceArchitecture> listArchitecture(UUID workspaceId, UUID requestingUserId) {
        requireOwnedWorkspace(workspaceId, requestingUserId);
        return architectureRepository.findAllByWorkspaceId(workspaceId);
    }

    private Workspace requireOwnedWorkspace(UUID workspaceId, UUID requestingUserId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        if (!workspace.getOwnerId().equals(requestingUserId)) throw new WorkspaceNotFoundException(workspaceId);
        return workspace;
    }

    private WorkspaceArchitecture requireArchitectureInWorkspace(UUID architectureId, UUID workspaceId) {
        WorkspaceArchitecture architecture = architectureRepository.findById(architectureId)
                .orElseThrow(() -> new WorkspaceArchitectureNotFoundException(architectureId));
        if (!architecture.getWorkspaceId().equals(workspaceId)) throw new WorkspaceArchitectureNotFoundException(architectureId);
        return architecture;
    }
}