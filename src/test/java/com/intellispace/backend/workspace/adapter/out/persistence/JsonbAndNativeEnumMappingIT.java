package com.intellispace.backend.workspace.adapter.out.persistence;

import com.intellispace.backend.testsupport.PostgresIntegrationTest;
import com.intellispace.backend.workspace.domain.Enum.ArchitecturalType;
import com.intellispace.backend.workspace.domain.Enum.WallSide;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE) // without this, @DataJpaTest silently swaps in an embedded H2 — exactly the thing this test exists to NOT use
class JsonbAndNativeEnumMappingIT extends PostgresIntegrationTest {

    @Autowired TestEntityManager entityManager;
    @Autowired WorkspaceFurnitureJpaRepository furnitureRepository;
    @Autowired WorkspaceArchitectureJpaRepository architectureRepository;

    @Test
    void materialOverrides_roundTripsThroughJsonb() {
        UUID workspaceId = persistTestWorkspace();
        WorkspaceFurnitureEntity entity = WorkspaceFurnitureEntity.builder()
                .workspaceId(workspaceId).catalogItemId(UUID.randomUUID())
                .materialOverrides(Map.of("body", Map.of("color", "#334455", "material", "velvet")))
                .build();
        entity.setId(UUID.randomUUID());
        UUID savedId = furnitureRepository.saveAndFlush(entity).getId();

        entityManager.clear(); // force a genuine DB round trip, not a cache hit on the same Java object

        Map<String, Object> reloaded = furnitureRepository.findById(savedId).orElseThrow().getMaterialOverrides();
        assertThat(reloaded).containsEntry("body", Map.of("color", "#334455", "material", "velvet"));
    }

    @Test
    void nativeEnumColumns_roundTrip() {
        UUID workspaceId = persistTestWorkspace();
        WorkspaceArchitectureEntity entity = WorkspaceArchitectureEntity.builder()
                .workspaceId(workspaceId).elementType(ArchitecturalType.window).wall(WallSide.left)
                .wallPosition(1.2).width(0.9).height(1.4).sillHeight(0.8)
                .build();
        entity.setId(UUID.randomUUID());
        UUID savedId = architectureRepository.saveAndFlush(entity).getId();

        entityManager.clear();

        WorkspaceArchitectureEntity reloaded = architectureRepository.findById(savedId).orElseThrow();
        assertThat(reloaded.getElementType()).isEqualTo(ArchitecturalType.window);
        assertThat(reloaded.getWall()).isEqualTo(WallSide.left);
    }

    private UUID persistTestWorkspace() {
        WorkspaceEntity workspace = WorkspaceEntity.builder()
                .userId(UUID.randomUUID()).name("Fixture")
                .roomWidth(4).roomDepth(5).roomHeight(2.7).wallThickness(0.15)
                .wallColor("#FFFFFF").floorColor("#8B5A2B").ceilingColor("#FFFFFF")
                .build();
        workspace.setId(UUID.randomUUID());
        return entityManager.persistAndFlush(workspace).getId();
    }
}
