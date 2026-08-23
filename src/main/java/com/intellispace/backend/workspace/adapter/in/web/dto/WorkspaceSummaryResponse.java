package com.intellispace.backend.workspace.adapter.in.web.dto;

import java.util.UUID;

public record WorkspaceSummaryResponse(UUID id, String name, String roomType, String designStyle) {}