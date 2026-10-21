package com.intellispace.backend.catalog.adapter.in.web;

import com.intellispace.backend.testsupport.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CatalogApiIT extends PostgresIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void catalogIsPublicAndReturnsStableIdsAndModelMetadata() throws Exception {
        mockMvc.perform(get("/api/catalog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].slug").isNotEmpty())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].category").isNotEmpty())
                .andExpect(jsonPath("$[0].modelPath").isNotEmpty())
                .andExpect(jsonPath("$[0].dimensions").exists())
                .andExpect(jsonPath("$[0].defaultScale.length()").value(3))
                .andExpect(jsonPath("$[0].defaultRotation.length()").value(3));
    }
}
