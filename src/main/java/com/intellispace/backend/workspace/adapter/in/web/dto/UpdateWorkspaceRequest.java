package com.intellispace.backend.workspace.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateWorkspaceRequest(
        String name,
        AppearanceDto appearance,
        java.math.BigDecimal budget,
        String currency,
        @NotNull Integer expectedVersion
) {
    public record AppearanceDto(String wallColor, String floorColor, String ceilingColor, String lightPreset) {}
}