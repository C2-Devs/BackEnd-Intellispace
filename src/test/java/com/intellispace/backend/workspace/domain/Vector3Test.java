package com.intellispace.backend.workspace.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.intellispace.backend.workspace.domain.Record.Vector3;

class Vector3Test {
    @Nested
    class Construction {
        @Test
        void acceptsNegativeValues() {
            Vector3 vec = new Vector3(-1.5, -2.0, 0.5);
            assertThat(vec.x()).isEqualTo(-1.5);
        }

        @Test
        void rejectsNaN() {
            assertThatThrownBy(() -> new Vector3(Double.NaN, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejectsInfinity() {
            assertThatThrownBy(() -> new Vector3(Double.POSITIVE_INFINITY, 1, 1)).isInstanceOf(IllegalArgumentException.class);
        }
    }
    
    @Test 
    void zeroConstant_isZeroZeroZero() { 
        assertThat(Vector3.ZERO).isEqualTo(new Vector3(0, 0, 0)); 
    }
}
