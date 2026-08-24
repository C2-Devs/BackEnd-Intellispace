package com.intellispace.backend.workspace.domain;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import com.intellispace.backend.workspace.domain.Record.Vector3;
import com.intellispace.backend.workspace.domain.Record.Scale3;
import com.intellispace.backend.workspace.domain.exception.FurnitureLockedException;

class WorkspaceFurnitureTest {

    private final UUID workspaceId = UUID.randomUUID();
    private final UUID catalogItemId = UUID.randomUUID();

    private WorkspaceFurniture placed() {
        return WorkspaceFurniture.place(workspaceId, catalogItemId, Vector3.ZERO, Vector3.ZERO, Scale3.UNIT);
    }

    @Test
    void place_startsUnlockedAndVisible() {
        WorkspaceFurniture furniture = placed();
        assertThat(furniture.isLocked()).isFalse();
        assertThat(furniture.isVisible()).isTrue();
    }

    @Test
    void moveTo_updatesPositionWhenUnlocked() {
        WorkspaceFurniture furniture = placed();
        Vector3 newPosition = new Vector3(1.2, 0, -0.8);
        furniture.moveTo(newPosition);
        assertThat(furniture.getPosition()).isEqualTo(newPosition);
    }

    @Test
    void moveTo_throwsWhenLocked() {
        WorkspaceFurniture furniture = placed();
        furniture.lock();
        assertThatThrownBy(() -> furniture.moveTo(new Vector3(1, 0, 0)))
                .isInstanceOf(FurnitureLockedException.class);
    }

    @Test
    void rotateTo_throwsWhenLocked() {
        WorkspaceFurniture furniture = placed();
        furniture.lock();
        assertThatThrownBy(() -> furniture.rotateTo(new Vector3(0, 90, 0)))
                .isInstanceOf(FurnitureLockedException.class);
    }

    @Test
    void scaleTo_throwsWhenLocked() {
        WorkspaceFurniture furniture = placed();
        furniture.lock();
        assertThatThrownBy(() -> furniture.scaleTo(new Scale3(2, 2, 2)))
                .isInstanceOf(FurnitureLockedException.class);
    }

    @Test
    void unlock_restoresAbilityToTransform() {
        WorkspaceFurniture furniture = placed();
        furniture.lock();
        furniture.unlock();
        Vector3 newPosition = new Vector3(3, 0, 3);
        assertThatCode(() -> furniture.moveTo(newPosition)).doesNotThrowAnyException();
        assertThat(furniture.getPosition()).isEqualTo(newPosition);
    }

    @Test
    void hide_and_show_workRegardlessOfLockState() {
        // The specific design decision from Step 3: locking blocks transforms, never visibility.
        WorkspaceFurniture furniture = placed();
        furniture.lock();
        assertThatCode(furniture::hide).doesNotThrowAnyException();
        assertThat(furniture.isVisible()).isFalse();
        assertThatCode(furniture::show).doesNotThrowAnyException();
        assertThat(furniture.isVisible()).isTrue();
    }

    @Test
    void equality_isByIdNotByFieldValues() {
        UUID sharedId = UUID.randomUUID();
        WorkspaceFurniture a = WorkspaceFurniture.reconstruct(sharedId, workspaceId, catalogItemId,
                Vector3.ZERO, Vector3.ZERO, Scale3.UNIT, false, true, null);
        WorkspaceFurniture b = WorkspaceFurniture.reconstruct(sharedId, workspaceId, catalogItemId,
                new Vector3(9, 9, 9), Vector3.ZERO, Scale3.UNIT, true, false, null);
        assertThat(a).isEqualTo(b);
    }
}
