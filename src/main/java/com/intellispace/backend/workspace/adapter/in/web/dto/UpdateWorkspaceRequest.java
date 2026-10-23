package com.intellispace.backend.workspace.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateWorkspaceRequest(
        String name,
        @Valid RoomDto room,
        AppearanceDto appearance,
        BigDecimal budget,
        String currency,
        Map<String, Object> editorState,
        @NotNull Integer expectedVersion
) {
    public record RoomDto(
            @Positive double width,
            @Positive double depth,
            @Positive double height,
            @Positive double wallThickness
    ) {}

    public record AppearanceDto(
            String wallColor,
            String floorColor,
            String ceilingColor,
            String lightPreset
    ) {}
}
