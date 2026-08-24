package com.intellispace.backend.workspace.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import com.intellispace.backend.workspace.domain.Record.RoomGeometry;
import com.intellispace.backend.workspace.domain.Record.RoomAppearance;

class WorkspaceTest {

    private final RoomGeometry geometry = new RoomGeometry(4, 5, 2.7, 0.15);
    private final RoomAppearance appearance = new RoomAppearance("#FFFFFF", "#8B5A2B", "#000000", "day");

    @Test
    void create_assignsIdAndStartsAtVersionZero() {
        Workspace workspace = Workspace.create(UUID.randomUUID(), "Living Room", null, null, null, geometry, appearance, null);
        assertThat(workspace.getId()).isNotNull();
        assertThat(workspace.getVersion()).isZero();
    }

    @Test
    void rename_updatesName() {
        Workspace workspace = Workspace.create(UUID.randomUUID(), "Original", null, null, null, geometry, appearance, null);
        workspace.rename("Updated");
        assertThat(workspace.getName()).isEqualTo("Updated");
    }

    @Test
    void rename_rejectsBlankName() {
        Workspace workspace = Workspace.create(UUID.randomUUID(), "Original", null, null, null, geometry, appearance, null);
        assertThatThrownBy(() -> workspace.rename("   ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equality_isByIdNotByFieldValues() {
        UUID sharedId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        Workspace a = Workspace.reconstruct(sharedId, owner, "Name A", null, null, null, geometry, appearance, null, 0);
        Workspace b = Workspace.reconstruct(sharedId, owner, "Name B", null, null, null, geometry, appearance, null, 5);

        // Same id, completely different name and version — still the same workspace. If this were a
        // record with structural equality instead, this assertion would fail, and that failure is
        // exactly why Workspace is a hand-written class, not a record.
        assertThat(a).isEqualTo(b);
    }

    @Test
    void equality_differsWhenIdsDiffer() {
        Workspace a = Workspace.create(UUID.randomUUID(), "Same Name", null, null, null, geometry, appearance, null);
        Workspace b = Workspace.create(UUID.randomUUID(), "Same Name", null, null, null, geometry, appearance, null);
        assertThat(a).isNotEqualTo(b);
    }
}
