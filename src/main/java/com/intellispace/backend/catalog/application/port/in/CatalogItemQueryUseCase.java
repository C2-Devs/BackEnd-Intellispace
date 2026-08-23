package com.intellispace.backend.catalog.application.port.in;

import java.util.UUID;

public interface CatalogItemQueryUseCase {
    boolean existsById(UUID catalogItemId);
}