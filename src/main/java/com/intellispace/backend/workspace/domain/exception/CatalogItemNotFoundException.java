package com.intellispace.backend.workspace.domain.exception;

import java.util.UUID;

public class CatalogItemNotFoundException extends RuntimeException {
    public CatalogItemNotFoundException(UUID catalogItemId) {
        super("Catalog item not found: " + catalogItemId);
    }
}