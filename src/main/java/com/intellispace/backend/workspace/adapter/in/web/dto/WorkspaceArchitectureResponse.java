package com.intellispace.backend.workspace.adapter.in.web.dto;

import com.intellispace.backend.workspace.domain.Enum.ArchitecturalType;
import com.intellispace.backend.workspace.domain.Enum.WallSide;

import java.util.UUID;

public record WorkspaceArchitectureResponse(
        UUID id, ArchitecturalType elementType, WallSide wall,
        double alongWall, double fromFloor, double width, double height
) {}
