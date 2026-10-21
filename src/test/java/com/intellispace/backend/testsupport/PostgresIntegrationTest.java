package com.intellispace.backend.testsupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

public abstract class PostgresIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));

    static {
        POSTGRES.start();
        // Deliberately never POSTGRES.stop() — Testcontainers registers its own Ryuk sidecar
        // container as a JVM shutdown hook that tears this down automatically when the test
        // run ends. This is the documented "singleton container" pattern, not a resource leak.
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("jwt.secret", () -> "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
    }

    protected UUID insertTestUser() {
        UUID userId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO app_user (id, email, password_hash, display_name) VALUES (?, ?, ?, ?)",
                userId,
                "integration-" + userId + "@example.test",
                "unused-test-password-hash",
                "Integration Test");
        return userId;
    }

    protected UUID insertTestCatalogItem() {
        UUID itemId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO catalog_item (id, slug, name, category) VALUES (?, ?, ?, ?)",
                itemId,
                "integration-" + itemId,
                "Integration Test Item",
                "test");
        return itemId;
    }
}
