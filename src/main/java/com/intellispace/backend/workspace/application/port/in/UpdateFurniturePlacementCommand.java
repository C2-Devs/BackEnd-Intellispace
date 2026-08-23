package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.Record.Scale3;
import com.intellispace.backend.workspace.domain.Record.Vector3;

import java.util.Map;

public record UpdateFurniturePlacementCommand(
        Vector3 position, Vector3 rotation, Scale3 scale,
        Boolean locked, Boolean visible, Map<String, Object> materialOverrides
) {}
