package com.intellispace.backend.workspace.adapter.out.catalog;

import com.intellispace.backend.catalog.adapter.out.persistence.CatalogItemJpaRepository;
import com.intellispace.backend.workspace.application.port.out.CatalogItemLookupPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CatalogItemLookupAdapter implements CatalogItemLookupPort {

    private final CatalogItemJpaRepository repository;

    public CatalogItemLookupAdapter(CatalogItemJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean exists(UUID catalogItemId) {
        return repository.existsById(catalogItemId);
    }

    @Override
    public boolean isAvailableInWorkspace(UUID catalogItemId, UUID workspaceId) {
        return repository.findById(catalogItemId)
                .map(item -> item.getWorkspaceId() == null
                        || workspaceId.equals(item.getWorkspaceId()))
                .orElse(false);
    }
}
