package com.intellispace.backend.workspace.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddFurnitureRequest(
        @NotNull UUID catalogItemId,
        @NotNull @Valid XyzDto position,
        @Valid XyzDto rotation,
        @Valid XyzDto scale
) {}