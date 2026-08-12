package com.intellispace.backend.workspace.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
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
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Builder.Default
    @Column(name = "wall_thickness", nullable = false)
    private double wallThickness = 0.15;

    @Column(name = "wall_color", nullable = false, length = 7)
    private String wallColor;

    @Column(name = "floor_color", nullable = false, length = 7)
    private String floorColor;

    @Column(name = "ceiling_color", nullable = false, length = 7)
    private String ceilingColor;

    @Builder.Default
    @Column(name = "light_preset", nullable = false, length = 50)
    private String lightPreset = "day";

    @Column(precision = 12, scale = 2)
    private BigDecimal budget;

    @Builder.Default
    @Column(length = 3)
    private String currency = "INR";

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



}
