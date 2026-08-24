package com.intellispace.backend.workspace.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.intellispace.backend.workspace.domain.Record.RoomGeometry;

class RoomGeometryTest {
    @Test void rejectsZeroWidth() {
        assertThatThrownBy(() -> new RoomGeometry(0, 5, 2.7, 0.15)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test void rejectsZeroDepth() {
        assertThatThrownBy(() -> new RoomGeometry(4, 0, 2.7, 0.15)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test void rejectsZeroHeight() {
        assertThatThrownBy(() -> new RoomGeometry(4, 5, 0, 0.15)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test void rejectsZeroWallThickness() {
        assertThatThrownBy(() -> new RoomGeometry(4, 5, 2.7, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
