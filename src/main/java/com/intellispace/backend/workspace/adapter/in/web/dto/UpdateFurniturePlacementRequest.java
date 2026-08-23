package com.intellispace.backend.workspace.adapter.in.web.dto;

import jakarta.validation.Valid;
import java.util.Map;

public record UpdateFurniturePlacementRequest(
        @Valid XyzDto position, @Valid XyzDto rotation, @Valid XyzDto scale,
        Boolean locked, Boolean visible, Map<String, Object> materialOverrides
) {}