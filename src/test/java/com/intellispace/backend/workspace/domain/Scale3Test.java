package com.intellispace.backend.workspace.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.intellispace.backend.workspace.domain.Record.Scale3;

class Scale3Test {

    @Nested
    class Construction {
        @Test
        void acceptsPositiveValues() {
            Scale3 scale = new Scale3(1.5, 2.0, 0.5);
            assertThat(scale.x()).isEqualTo(1.5);
        }

        @Test
        void rejectsZero() {
            assertThatThrownBy(() -> new Scale3(0, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejectsNegative() {
            assertThatThrownBy(() -> new Scale3(1, -0.1, 1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejectsNaN() {
            assertThatThrownBy(() -> new Scale3(Double.NaN, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejectsInfinity() {
            assertThatThrownBy(() -> new Scale3(Double.POSITIVE_INFINITY, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void unitConstant_isOneOneOne() {
        assertThat(Scale3.UNIT).isEqualTo(new Scale3(1, 1, 1));
    }
}
