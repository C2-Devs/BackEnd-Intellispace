package com.intellispace.backend.workspace.application.port.in;

import com.intellispace.backend.workspace.domain.*;
import com.intellispace.backend.workspace.domain.Enum.ArchitecturalType;
import com.intellispace.backend.workspace.domain.Enum.WallSide;
import com.intellispace.backend.workspace.domain.Record.OpeningDimensions;
import com.intellispace.backend.workspace.domain.Record.WallOffset;

import java.util.List;
import java.util.UUID;

public record AddArchitectureCommand(UUID workspaceId, ArchitecturalType elementType, WallSide wall,
                                     WallOffset offset, OpeningDimensions dimensions) {}

