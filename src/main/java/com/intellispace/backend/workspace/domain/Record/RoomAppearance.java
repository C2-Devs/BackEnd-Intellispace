package com.intellispace.backend.workspace.domain.Record;
import java.util.regex.Pattern;

public record RoomAppearance(String wallColor, String floorColor, String ceilingColor, String lightPreset) {

    private static final Pattern HEX_COLOR = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    public RoomAppearance {
        requireHexColor(wallColor, "wallColor");
        requireHexColor(floorColor, "floorColor");
        requireHexColor(ceilingColor, "ceilingColor");
        if (lightPreset == null || lightPreset.isBlank()) {
            throw new IllegalArgumentException("lightPreset must not be blank");
        }
    }

    private static void requireHexColor(String value, String fieldName) {
        if (value == null || !HEX_COLOR.matcher(value).matches()) {
            throw new IllegalArgumentException(fieldName + " must be a 6-digit hex color like #FFFFFF, got: " + value);
        }
    }
}