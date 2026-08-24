package com.intellispace.backend.workspace.application.service;

import com.intellispace.backend.testsupport.PostgresIntegrationTest;
import com.intellispace.backend.workspace.application.port.in.CreateWorkspaceCommand;
import com.intellispace.backend.workspace.application.port.in.UpdateWorkspaceCommand;
import com.intellispace.backend.workspace.domain.Record.RoomAppearance;
import com.intellispace.backend.workspace.domain.Record.RoomGeometry;
import com.intellispace.backend.workspace.domain.Workspace;
import com.intellispace.backend.workspace.domain.exception.StaleWorkspaceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class WorkspaceApplicationServiceIT extends PostgresIntegrationTest {

    @Autowired WorkspaceApplicationService workspaceApplicationService;

    @Test
    void updateWorkspace_rejectsAStaleClientSubmittedVersion() {
        UUID ownerId = UUID.randomUUID();
        Workspace created = workspaceApplicationService.createWorkspace(new CreateWorkspaceCommand(
                ownerId, "Living Room", null, null, null,
                new RoomGeometry(4, 5, 2.7, 0.15),
                new RoomAppearance("#FFFFFF", "#8B5A2B", "#FFFFFF", "day"), null));

        workspaceApplicationService.updateWorkspace(created.getId(), ownerId,
                new UpdateWorkspaceCommand("First rename", null, null, 0)); // succeeds, version 0 -> 1

        assertThatThrownBy(() -> workspaceApplicationService.updateWorkspace(created.getId(), ownerId,
                new UpdateWorkspaceCommand("Second rename", null, null, 0))) // still claiming version 0
                .isInstanceOf(StaleWorkspaceException.class);
    }
}
