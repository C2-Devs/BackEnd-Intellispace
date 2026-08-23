package com.intellispace.backend.workspace.application.port.out;

import java.util.UUID;

public interface CatalogItemLookupPort {
    boolean exists(UUID catalogItemId);
}