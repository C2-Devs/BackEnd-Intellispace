package com.intellispace.backend.workspace.adapter.out.persistence;

import com.intellispace.backend.workspace.application.port.out.WorkspaceRepository;
import com.intellispace.backend.workspace.domain.Workspace;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WorkspaceRepositoryAdapter implements WorkspaceRepository {

    private final WorkspaceJpaRepository jpaRepository;
    private final WorkspaceMapper mapper;

    public WorkspaceRepositoryAdapter(WorkspaceJpaRepository jpaRepository, WorkspaceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Workspace save(Workspace workspace) {
        WorkspaceEntity entity = jpaRepository.findById(workspace.getId())
                .map(existing -> mapper.updateEntity(existing, workspace))
                .orElseGet(() -> mapper.toNewEntity(workspace));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Workspace> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Workspace> findAllByOwnerId(UUID ownerId) {
        return jpaRepository.findAllByUserId(ownerId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}