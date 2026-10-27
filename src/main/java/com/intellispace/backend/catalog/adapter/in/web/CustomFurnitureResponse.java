package com.intellispace.backend.catalog.adapter.in.web;

import java.util.List;
import java.util.UUID;

public record CustomFurnitureResponse(
        UUID id,
        UUID workspaceId,
        String slug,
        String name,
        String category,
        String description,
        String modelPath,
        String thumbnailPath,
        Dimensions dimensions,
        List<Double> defaultScale,
        List<Double> defaultRotation,
        double snapHeight,
        boolean custom) {

    public record Dimensions(double width, double height, double depth) {
    }
}
