package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.Record.Scale3;
import com.intellispace.backend.workspace.domain.Record.Vector3;

import java.util.Map;
import java.util.UUID;

public record AddFurnitureCommand(
        UUID workspaceId, UUID catalogItemId, Vector3 position, Vector3 rotation, Scale3 scale
) {}

