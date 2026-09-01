package com.intellispace.backend.workspace.adapter.in.web.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateArchitectureRequest(OffsetDto offset, DimensionsDto dimensions) {
    public record OffsetDto(@PositiveOrZero double alongWall, @PositiveOrZero double fromFloor) {}
    public record DimensionsDto(@Positive double width, @Positive double height) {}
}
