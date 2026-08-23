package com.intellispace.backend.workspace.adapter.out.persistence;

import com.intellispace.backend.workspace.domain.Enum.ArchitecturalType;
import com.intellispace.backend.workspace.domain.Enum.WallSide;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspace_architecture")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class WorkspaceArchitectureEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "element_type", nullable = false)
    private ArchitecturalType elementType;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private WallSide wall;

    @Column(name = "wall_position", nullable = false)
    private double wallPosition;

    @Column(nullable = false)
    private double width;

    @Column(nullable = false)
    private double height;

    @Builder.Default
    @Column(name = "sill_height", nullable = false)
    private double sillHeight = 0.0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder
    public WorkspaceArchitectureEntity(UUID workspaceId, ArchitecturalType elementType, WallSide wall,
                                       double wallPosition, double width, double height, double sillHeight) {
        this.workspaceId = workspaceId;
        this.elementType = elementType;
        this.wall = wall;
        this.wallPosition = wallPosition;
        this.width = width;
        this.height = height;
        this.sillHeight = sillHeight;
    }

}
