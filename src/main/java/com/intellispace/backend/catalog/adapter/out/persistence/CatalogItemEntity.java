package com.intellispace.backend.catalog.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_item")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CatalogItemEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "model_path", columnDefinition = "TEXT")
    private String modelPath;

    @Column(name = "base_width")
    private Double baseWidth;

    @Column(name = "base_depth")
    private Double baseDepth;

    @Column(name = "base_height")
    private Double baseHeight;

    @Builder.Default
    @Column(name = "default_scale_x", nullable = false)
    private double defaultScaleX = 1.0;

    @Builder.Default
    @Column(name = "default_scale_y", nullable = false)
    private double defaultScaleY = 1.0;

    @Builder.Default
    @Column(name = "default_scale_z", nullable = false)
    private double defaultScaleZ = 1.0;

    @Builder.Default
    @Column(name = "default_rot_x", nullable = false)
    private double defaultRotX = 0.0;

    @Builder.Default
    @Column(name = "default_rot_y", nullable = false)
    private double defaultRotY = 0.0;

    @Builder.Default
    @Column(name = "default_rot_z", nullable = false)
    private double defaultRotZ = 0.0;

    @Builder.Default
    @Column(name = "snap_height", nullable = false)
    private double snapHeight = 0.0;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Builder.Default
    @Column(length = 3)
    private String currency = "INR";

    @Column(length = 50)
    private String style;

    @Column(length = 100)
    private String material;

    @Column(length = 50)
    private String color;

    @Column(name = "room_type", length = 50)
    private String roomType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder
    public CatalogItemEntity(String slug, String name, String category, String description, String modelPath,
                             Double baseWidth, Double baseDepth, Double baseHeight,
                             double defaultScaleX, double defaultScaleY, double defaultScaleZ,
                             double defaultRotX, double defaultRotY, double defaultRotZ,
                             double snapHeight, BigDecimal price, String currency,
                             String style, String material, String color, String roomType) {
        this.slug = slug;
        this.name = name;
        this.category = category;
        this.description = description;
        this.modelPath = modelPath;
        this.baseWidth = baseWidth;
        this.baseDepth = baseDepth;
        this.baseHeight = baseHeight;
        this.defaultScaleX = defaultScaleX;
        this.defaultScaleY = defaultScaleY;
        this.defaultScaleZ = defaultScaleZ;
        this.defaultRotX = defaultRotX;
        this.defaultRotY = defaultRotY;
        this.defaultRotZ = defaultRotZ;
        this.snapHeight = snapHeight;
        this.price = price;
        this.currency = currency;
        this.style = style;
        this.material = material;
        this.color = color;
        this.roomType = roomType;
    }

}