package com.intellispace.backend.workspace.adapter.out.persistence;

import com.intellispace.backend.workspace.application.port.out.WorkspaceArchitectureRepository;
import com.intellispace.backend.workspace.domain.WorkspaceArchitecture;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WorkspaceArchitectureRepositoryAdapter implements WorkspaceArchitectureRepository {

    private final WorkspaceArchitectureJpaRepository jpaRepository;
    private final WorkspaceArchitectureMapper mapper;

    public WorkspaceArchitectureRepositoryAdapter(WorkspaceArchitectureJpaRepository jpaRepository, WorkspaceArchitectureMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceArchitecture save(WorkspaceArchitecture architecture) {
        var entity = jpaRepository.findById(architecture.getId())
                .map(existing -> mapper.updateEntity(existing, architecture))
                .orElseGet(() -> mapper.toNewEntity(architecture));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<WorkspaceArchitecture> findById(UUID id) { return jpaRepository.findById(id).map(mapper::toDomain); }

    @Override
    public List<WorkspaceArchitecture> findAllByWorkspaceId(UUID workspaceId) {
        return jpaRepository.findAllByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}