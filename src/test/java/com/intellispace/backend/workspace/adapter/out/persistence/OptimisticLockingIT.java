package com.intellispace.backend.workspace.adapter.out.persistence;

import com.intellispace.backend.testsupport.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class OptimisticLockingIT extends PostgresIntegrationTest {

    @Autowired TestEntityManager entityManager;
    @Autowired WorkspaceJpaRepository jpaRepository;

    @Test
    void hibernateVersionCheck_rejectsAConcurrentlyStaleWrite() {
        WorkspaceEntity fresh = WorkspaceEntity.builder()
                .id(UUID.randomUUID()).userId(UUID.randomUUID()).name("Original")
                .roomWidth(4).roomDepth(5).roomHeight(2.7).wallThickness(0.15)
                .wallColor("#FFFFFF").floorColor("#8B5A2B").ceilingColor("#FFFFFF")
                .build();
        UUID id = jpaRepository.saveAndFlush(fresh).getId();
        entityManager.clear();

        // Two independent reads of the same row, simulating two separate requests each starting
        // its own transaction from scratch — not two views of one cached Java object.
        WorkspaceEntity readByRequestA = jpaRepository.findById(id).orElseThrow();
        entityManager.clear();
        WorkspaceEntity readByRequestB = jpaRepository.findById(id).orElseThrow();

        readByRequestA.setName("Changed by A");
        jpaRepository.saveAndFlush(readByRequestA); // DB moves from version 0 to version 1

        readByRequestB.setName("Changed by B");
        // readByRequestB still carries version 0 in memory. Hibernate generates
        // UPDATE ... SET version = 1 WHERE id = ? AND version = 0 — zero rows match,
        // since the DB is already at version 1 — and Spring translates that into this exception.
        assertThatThrownBy(() -> jpaRepository.saveAndFlush(readByRequestB))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}
