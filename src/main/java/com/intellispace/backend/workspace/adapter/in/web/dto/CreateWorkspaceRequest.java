package com.intellispace.backend.workspace.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateWorkspaceRequest(
        @NotBlank @Size(max = 200) String name,
        String description,
        String roomType,
        String designStyle,
        @NotNull @Valid RoomDto room,
        @NotNull @Valid AppearanceDto appearance,
        BigDecimal budget,
        String currency
) {
    public record RoomDto(@Positive double width, @Positive double depth, @Positive double height, @Positive double wallThickness) {}
    public record AppearanceDto(@NotBlank String wallColor, @NotBlank String floorColor, @NotBlank String ceilingColor, String lightPreset) {}
}