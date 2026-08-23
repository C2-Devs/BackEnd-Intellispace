package com.intellispace.backend.workspace.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record WorkspaceDetailResponse(
        UUID id, String name, String description,
        RoomDto room, AppearanceDto appearance,
        BigDecimal budget, String currency,
        List<WorkspaceFurnitureResponse> furniture,
        List<Object> architecture // always [] until WorkspaceArchitecture's ports exist
) {
    public record RoomDto(double width, double depth, double height, double wallThickness) {}
    public record AppearanceDto(String wallColor, String floorColor, String ceilingColor, String lightPreset) {}
}