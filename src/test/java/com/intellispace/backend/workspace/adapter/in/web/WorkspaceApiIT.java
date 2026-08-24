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

    private String registerAndGetToken(String email) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"correcthorsebattery\",\"displayName\":\"Test\"}";
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}
