package com.intellispace.backend.catalog.application.port.in;

import com.intellispace.backend.catalog.domain.CatalogItem;

import java.util.List;
import java.util.UUID;

public interface CatalogItemQueryUseCase {
    boolean existsById(UUID catalogItemId);

    List<CatalogItem> listCatalogItems();
}
