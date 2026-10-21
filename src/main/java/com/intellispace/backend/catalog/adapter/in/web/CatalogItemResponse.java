package com.intellispace.backend.catalog.adapter.in.web;

import com.intellispace.backend.catalog.domain.CatalogItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CatalogItemResponse(
        UUID id,
        String slug,
        String name,
        String category,
        String description,
        String modelPath,
        Dimensions dimensions,
        List<Double> defaultScale,
        List<Double> defaultRotation,
        double snapHeight,
        BigDecimal price,
        String currency,
        String style,
        String material,
        String color,
        String roomType
) {
    public record Dimensions(Double width, Double depth, Double height) {}

    public static CatalogItemResponse from(CatalogItem item) {
        return new CatalogItemResponse(
                item.id(),
                item.slug(),
                item.name(),
                item.category(),
                item.description(),
                item.modelPath(),
                new Dimensions(item.baseWidth(), item.baseDepth(), item.baseHeight()),
                List.of(item.defaultScaleX(), item.defaultScaleY(), item.defaultScaleZ()),
                List.of(item.defaultRotX(), item.defaultRotY(), item.defaultRotZ()),
                item.snapHeight(),
                item.price(),
                item.currency(),
                item.style(),
                item.material(),
                item.color(),
                item.roomType());
    }
}
