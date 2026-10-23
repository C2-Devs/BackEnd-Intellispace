package com.intellispace.backend.workspace.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellispace.backend.testsupport.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class WorkspaceApiIT extends PostgresIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void fullLifecycle_ownershipAndVersioningEnforcedThroughRealHttp() throws Exception {
        String ownerToken = registerAndGetToken("owner@test.com");
        String intruderToken = registerAndGetToken("intruder@test.com");

        String createBody = """
            {"name":"Living Room",
             "room":{"width":4,"depth":5,"height":2.7,"wallThickness":0.15},
             "appearance":{"wallColor":"#FFFFFF","floorColor":"#8B5A2B","ceilingColor":"#FFFFFF","lightPreset":"day"}}""";

        MvcResult created = mockMvc.perform(post("/api/workspaces")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.version").value(0))
                .andReturn();
        UUID workspaceId = UUID.fromString(objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText());

        // No token at all — the filter chain rejects this before it ever reaches the controller.
        mockMvc.perform(get("/api/workspaces/" + workspaceId)).andExpect(status().isUnauthorized());

        // A different, authenticated, non-owning user — 404, not 403, per the Step 4/5 design decision.
        mockMvc.perform(get("/api/workspaces/" + workspaceId).header("Authorization", "Bearer " + intruderToken))
                .andExpect(status().isNotFound());

        // Owner updates at the version they were just given — succeeds.
        mockMvc.perform(patch("/api/workspaces/" + workspaceId)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Renamed\",\"expectedVersion\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1));

        // Owner tries again, still holding the now-stale version 0 — rejected.
        mockMvc.perform(patch("/api/workspaces/" + workspaceId)
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Stale\",\"expectedVersion\":0}"))
                .andExpect(status().isConflict());
    }

    @Test
    void listReturnsOnlyOwnedWorkspacesAndValidationUsesProblemDetail() throws Exception {
        String ownerToken = registerAndGetToken("list-owner@test.com");
        String otherToken = registerAndGetToken("list-other@test.com");

        String createBody = """
            {"name":"Owner Room","room":{"width":4,"depth":5,"height":2.7,"wallThickness":0.15},
             "appearance":{"wallColor":"#FFFFFF","floorColor":"#8B5A2B","ceilingColor":"#FFFFFF","lightPreset":"day"}}""";
        mockMvc.perform(post("/api/workspaces")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/workspaces").header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Owner Room"))
                .andExpect(jsonPath("$[0].id").isNotEmpty());

        mockMvc.perform(get("/api/workspaces").header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(post("/api/workspaces")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"room\":null,\"appearance\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void editorStateAndRoomGeometryRoundTripThroughVersionedPatch() throws Exception {
        String token = registerAndGetToken("editor-state@test.com");
        String createBody = """
            {"name":"Editor Snapshot",
             "room":{"width":4,"depth":5,"height":2.7,"wallThickness":0.15},
             "appearance":{"wallColor":"#FFFFFF","floorColor":"#8B5A2B","ceilingColor":"#FFFFFF","lightPreset":"day"}}
            """;

        MvcResult created = mockMvc.perform(post("/api/workspaces")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.version").value(0))
                .andReturn();

        UUID workspaceId = UUID.fromString(
                objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asText());

        String editorState = """
            {
              "currentTemplate":"modern-studio",
              "room":{"floorMaterial":"floor-2","wallMaterial":"warmPaint"},
              "windows":[{"id":"window-1","wall":"back","position":0.5,"width":2.0,"height":1.5}],
              "doors":[{"id":"door-1","wall":"left","position":0.2,"width":1.0,"height":2.1}],
              "structures":[{"id":"partition-1","type":"partition","position":[1.0,1.2,0.0]}],
              "lightPreset":"day"
            }
            """;

        mockMvc.perform(patch("/api/workspaces/" + workspaceId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"room\":{\"width\":7.5,\"depth\":6.25,\"height\":3.1,\"wallThickness\":0.18},"
                                + "\"editorState\":" + editorState + ",\"expectedVersion\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.room.width").value(7.5))
                .andExpect(jsonPath("$.room.depth").value(6.25))
                .andExpect(jsonPath("$.room.height").value(3.1))
                .andExpect(jsonPath("$.editorState.currentTemplate").value("modern-studio"))
                .andExpect(jsonPath("$.editorState.windows[0].id").value("window-1"))
                .andExpect(jsonPath("$.editorState.doors[0].id").value("door-1"))
                .andExpect(jsonPath("$.editorState.structures[0].type").value("partition"));

        mockMvc.perform(get("/api/workspaces/" + workspaceId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1))
                .andExpect(jsonPath("$.room.width").value(7.5))
                .andExpect(jsonPath("$.editorState.currentTemplate").value("modern-studio"))
                .andExpect(jsonPath("$.editorState.windows[0].width").value(2.0));
    }

    private String registerAndGetToken(String email) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"correcthorsebattery\",\"displayName\":\"Test\"}";
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
