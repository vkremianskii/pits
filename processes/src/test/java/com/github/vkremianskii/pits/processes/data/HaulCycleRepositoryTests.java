package com.github.vkremianskii.pits.processes.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
class HaulCycleRepositoryTests {
    @Autowired
    HaulCycleRepository sut;

    @BeforeEach
    void cleanup() {
        sut.clear().block();
    }

    @Test
    void should_insert_and_get_last_minimal_haul_cycle() {
        // when
        sut.insert(1, null, null, null, null, null, null, null, null, null).block();
        var haulCycle = sut.getLastHaulCycleForTruck(1).block();

        // then
        assertThat(haulCycle).hasValueSatisfying(c -> {
            assertThat(c.getTruckId()).isEqualTo(1);
            assertThat(c.getInsertTimestamp()).isNotNull();
        });
    }

    @Test
    void should_insert_and_get_last_complete_haul_cycle() {
        // when
        sut.insert(1, 2, Instant.now(), Instant.now(), 41.1494512, -8.6107884, Instant.now(), 10, Instant.now(), Instant.now()).block();
        var haulCycle = sut.getLastHaulCycleForTruck(1).block();

        // then
        assertThat(haulCycle).hasValueSatisfying(c -> {
            assertThat(c.getTruckId()).isEqualTo(1);
            assertThat(c.getInsertTimestamp()).isNotNull();
            assertThat(c.getShovelId()).isEqualTo(2);
            assertThat(c.getWaitLoadTimestamp()).isNotNull();
            assertThat(c.getStartLoadTimestamp()).isNotNull();
            assertThat(c.getStartLoadLatitude()).isCloseTo(41.1494512, offset(1e-6));
            assertThat(c.getStartLoadLongitude()).isCloseTo(-8.6107884, offset(1e-6));
            assertThat(c.getEndLoadTimestamp()).isNotNull();
            assertThat(c.getEndLoadPayload()).isEqualTo(10);
            assertThat(c.getStartUnloadTimestamp()).isNotNull();
            assertThat(c.getEndUnloadTimestamp()).isNotNull();
        });
    }
}