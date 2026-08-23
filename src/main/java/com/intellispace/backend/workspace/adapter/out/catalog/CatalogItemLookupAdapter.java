package com.intellispace.backend.workspace.adapter.out.catalog;

import com.intellispace.backend.catalog.application.port.in.CatalogItemQueryUseCase;
import com.intellispace.backend.workspace.application.port.out.CatalogItemLookupPort;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class CatalogItemLookupAdapter implements CatalogItemLookupPort {

    private final CatalogItemQueryUseCase catalogItemQueryUseCase;

    public CatalogItemLookupAdapter(CatalogItemQueryUseCase catalogItemQueryUseCase) {
        this.catalogItemQueryUseCase = catalogItemQueryUseCase;
    }

    @Override
    public boolean exists(UUID catalogItemId) {
        return catalogItemQueryUseCase.existsById(catalogItemId);
    }
}