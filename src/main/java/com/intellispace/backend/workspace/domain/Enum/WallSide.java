package com.intellispace.backend.workspace.domain.Enum;

/** Mirrors the Postgres native enum `architectural_wall`. Lowercase is deliberate — see WorkspaceArchitectureEntity. */
public enum WallSide {
    left, right, front, back
}