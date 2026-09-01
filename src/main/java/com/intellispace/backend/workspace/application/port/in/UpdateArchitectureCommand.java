package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.Record.OpeningDimensions;
import com.intellispace.backend.workspace.domain.Record.WallOffset;

public record UpdateArchitectureCommand(WallOffset offset, OpeningDimensions dimensions) {}
