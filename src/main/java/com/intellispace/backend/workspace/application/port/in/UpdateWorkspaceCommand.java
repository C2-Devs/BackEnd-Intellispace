package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.Record.Money;
import com.intellispace.backend.workspace.domain.Record.RoomAppearance;
import com.intellispace.backend.workspace.domain.Record.RoomGeometry;

import java.util.Map;

public record UpdateWorkspaceCommand(
        String name,
        RoomAppearance appearance,
        Money budget,
        int expectedVersion,
        RoomGeometry geometry,
        Map<String, Object> editorState
) {
    public UpdateWorkspaceCommand(
            String name,
            RoomAppearance appearance,
            Money budget,
            int expectedVersion
    ) {
        this(name, appearance, budget, expectedVersion, null, null);
    }
}
