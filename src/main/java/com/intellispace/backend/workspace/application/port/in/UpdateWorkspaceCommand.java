package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.Record.Money;
import com.intellispace.backend.workspace.domain.Record.RoomAppearance;

public record UpdateWorkspaceCommand(String name, RoomAppearance appearance, Money budget, int expectedVersion) {}

