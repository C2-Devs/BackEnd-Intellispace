package com.intellispace.backend.workspace.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "workspace_furniture")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WorkspaceFurnitureEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "catalog_item_id", nullable = false)
    private UUID catalogItemId;

    @Builder.Default @Column(name = "pos_x", nullable = false) private double posX = 0.0;
    @Builder.Default @Column(name = "pos_y", nullable = false) private double posY = 0.0;
    @Builder.Default @Column(name = "pos_z", nullable = false) private double posZ = 0.0;

    @Builder.Default @Column(name = "rot_x", nullable = false) private double rotX = 0.0;
    @Builder.Default @Column(name = "rot_y", nullable = false) private double rotY = 0.0;
    @Builder.Default @Column(name = "rot_z", nullable = false) private double rotZ = 0.0;

    @Builder.Default @Column(name = "scale_x", nullable = false) private double scaleX = 1.0;
    @Builder.Default @Column(name = "scale_y", nullable = false) private double scaleY = 1.0;
    @Builder.Default @Column(name = "scale_z", nullable = false) private double scaleZ = 1.0;

    @Builder.Default
    @Column(name = "is_locked", nullable = false)
    private boolean locked = false;

    @Builder.Default
    @Column(name = "is_visible", nullable = false)
    private boolean visible = true;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "material_overrides", columnDefinition = "jsonb")
    private Map<String, Object> materialOverrides;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder
    public WorkspaceFurnitureEntity(UUID workspaceId, UUID catalogItemId,
                                    double posX, double posY, double posZ,
                                    double rotX, double rotY, double rotZ,
                                    double scaleX, double scaleY, double scaleZ,
                                    boolean locked, boolean visible,
                                    Map<String, Object> materialOverrides) {
        this.workspaceId = workspaceId;
        this.catalogItemId = catalogItemId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.locked = locked;
        this.visible = visible;
        this.materialOverrides = materialOverrides;
    }
}