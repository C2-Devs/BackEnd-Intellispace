package com.intellispace.backend.workspace.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.UUID;

public record WorkspaceFurnitureResponse(
        UUID id, UUID catalogItemId, XyzDto position, XyzDto rotation, XyzDto scale,
        @JsonProperty("isLocked") boolean locked,
        @JsonProperty("isVisible") boolean visible,
        Map<String, Object> materialOverrides
) {}