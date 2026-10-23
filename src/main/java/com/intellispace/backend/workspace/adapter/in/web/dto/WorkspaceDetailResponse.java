package com.intellispace.backend.workspace.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record WorkspaceDetailResponse(
        UUID id, String name, String description,
        RoomDto room, AppearanceDto appearance,
        BigDecimal budget, String currency, int version,
        List<WorkspaceFurnitureResponse> furniture,
        List<Object> architecture,
        Map<String, Object> editorState
) {
    public WorkspaceDetailResponse(
            UUID id, String name, String description,
            RoomDto room, AppearanceDto appearance,
            BigDecimal budget, String currency, int version,
            List<WorkspaceFurnitureResponse> furniture,
            List<Object> architecture
    ) {
        this(id, name, description, room, appearance, budget, currency, version,
                furniture, architecture, Map.of());
    }

    public record RoomDto(double width, double depth, double height, double wallThickness) {}
    public record AppearanceDto(String wallColor, String floorColor, String ceilingColor, String lightPreset) {}
}
