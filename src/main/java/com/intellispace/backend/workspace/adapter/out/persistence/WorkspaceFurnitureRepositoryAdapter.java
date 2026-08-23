package com.intellispace.backend.workspace.adapter.out.persistence;

import com.intellispace.backend.workspace.application.port.out.WorkspaceFurnitureRepository;
import com.intellispace.backend.workspace.domain.WorkspaceFurniture;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WorkspaceFurnitureRepositoryAdapter implements WorkspaceFurnitureRepository {

    private final WorkspaceFurnitureJpaRepository jpaRepository;
    private final WorkspaceFurnitureMapper mapper;

    public WorkspaceFurnitureRepositoryAdapter(WorkspaceFurnitureJpaRepository jpaRepository, WorkspaceFurnitureMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public WorkspaceFurniture save(WorkspaceFurniture furniture) {
        var entity = jpaRepository.findById(furniture.getId())
                .map(existing -> mapper.updateEntity(existing, furniture))
                .orElseGet(() -> mapper.toNewEntity(furniture));
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<WorkspaceFurniture> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<WorkspaceFurniture> findAllByWorkspaceId(UUID workspaceId) {
        return jpaRepository.findAllByWorkspaceId(workspaceId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}