package com.intellispace.backend.workspace.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspace")
@Getter
@Setter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WorkspaceEntity {

    @EqualsAndHashCode.Include
    @Id
    // id is always supplied by the domain — no @GeneratedValue here.
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "room_type", length = 50)
    private String roomType;

    @Column(name = "design_style", length = 50)
    private String designStyle;

    @Column(name = "room_width", nullable = false)
    private double roomWidth;

    @Column(name = "room_depth", nullable = false)
    private double roomDepth;

    @Column(name = "room_height", nullable = false)
    private double roomHeight;

    @Column(name = "wall_thickness", nullable = false)
    private double wallThickness;

    @Column(name = "wall_color", nullable = false, length = 7)
    private String wallColor;

    @Column(name = "floor_color", nullable = false, length = 7)
    private String floorColor;

    @Column(name = "ceiling_color", nullable = false, length = 7)
    private String ceilingColor;

    @Column(name = "light_preset", nullable = false, length = 50)
    private String lightPreset;

    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    @Column(length = 3)
    private String currency;

    /** Hibernate-managed optimistic-lock counter. Never a constructor/builder parameter. */
    @Version
    @Column(nullable = false)
    private int version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * The single, constructor-level builder. Does NOT include id, version, createdAt, updatedAt —
     * those are managed externally (id set via setId() by the mapper; others managed by Hibernate).
     */
    @Builder
    public WorkspaceEntity(UUID userId, String name, String description, String roomType, String designStyle,
                           double roomWidth, double roomDepth, double roomHeight, double wallThickness,
                           String wallColor, String floorColor, String ceilingColor, String lightPreset,
                           BigDecimal budget, String currency) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.roomType = roomType;
        this.designStyle = designStyle;
        this.roomWidth = roomWidth;
        this.roomDepth = roomDepth;
        this.roomHeight = roomHeight;
        this.wallThickness = wallThickness;
        this.wallColor = wallColor;
        this.floorColor = floorColor;
        this.ceilingColor = ceilingColor;
        this.lightPreset = lightPreset;
        this.budget = budget;
        this.currency = currency;
    }

}
