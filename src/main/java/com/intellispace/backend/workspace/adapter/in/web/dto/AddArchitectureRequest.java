package com.intellispace.backend.workspace.adapter.in.web.dto;

import com.intellispace.backend.workspace.domain.Enum.ArchitecturalType;
import com.intellispace.backend.workspace.domain.Enum.WallSide;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.UUID;

public record AddArchitectureRequest(
        @NotNull ArchitecturalType elementType, @NotNull WallSide wall,
        @PositiveOrZero double alongWall, @PositiveOrZero double fromFloor,
        @Positive double width, @Positive double height
) {}

