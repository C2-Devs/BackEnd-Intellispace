package com.intellispace.backend.catalog.domain;

import java.math.BigDecimal;
import java.util.UUID;

public record CatalogItem(
        UUID id,
        String slug,
        String name,
        String category,
        String description,
        String modelPath,
        Double baseWidth,
        Double baseDepth,
        Double baseHeight,
        double defaultScaleX,
        double defaultScaleY,
        double defaultScaleZ,
        double defaultRotX,
        double defaultRotY,
        double defaultRotZ,
        double snapHeight,
        BigDecimal price,
        String currency,
        String style,
        String material,
        String color,
        String roomType
) {}
