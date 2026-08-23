package com.intellispace.backend.workspace.application.port.in;

import java.util.UUID;

public interface RemoveFurnitureUseCase {
    void removeFurniture(UUID furnitureId);
}
