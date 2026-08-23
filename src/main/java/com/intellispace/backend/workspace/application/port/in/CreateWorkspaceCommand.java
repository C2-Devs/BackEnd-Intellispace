package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.Record.Money;
import com.intellispace.backend.workspace.domain.Record.RoomAppearance;
import com.intellispace.backend.workspace.domain.Record.RoomGeometry;

import java.util.UUID;

public record CreateWorkspaceCommand(
        UUID ownerId, String name, String description, String roomType, String designStyle,
        RoomGeometry geometry, RoomAppearance appearance, Money budget
) {}

