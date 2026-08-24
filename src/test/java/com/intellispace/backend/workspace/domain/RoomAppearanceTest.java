package com.intellispace.backend.workspace.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.intellispace.backend.workspace.domain.Record.RoomAppearance;

class RoomAppearanceTest {
    @Test void acceptsValidHexColors() {
        assertThatCode(() -> new RoomAppearance("#FFFFFF", "#000000", "#123456", "day")).doesNotThrowAnyException();
    }
    @Test void rejectsColorWithoutHash() {
        assertThatThrownBy(() -> new RoomAppearance("FFFFFF", "#8B5A2B", "#000000", "day"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test void rejectsThreeDigitHex() {
        // #FFF is valid CSS but not what the 7-char VARCHAR column and this regex expect — worth its own case
        assertThatThrownBy(() -> new RoomAppearance("#FFF", "#8B5A2B", "#000000", "day"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test void rejectsBlankLightPreset() {
        assertThatThrownBy(() -> new RoomAppearance("#FFFFFF", "#8B5A2B", "#000000", "  "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
