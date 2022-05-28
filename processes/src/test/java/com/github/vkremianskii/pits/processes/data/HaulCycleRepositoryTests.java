package com.github.vkremianskii.pits.processes.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
class HaulCycleRepositoryTests {

    @Autowired
    HaulCycleRepository sut;

    @Test
    void should_insert_and_get_last_minimal_haul_cycle() {
        // when
        var truckId = UUID.randomUUID();
        sut.insert(truckId, null, null, null, null, null, null, null, null, null).block();
        var haulCycle = sut.getLastHaulCycleForTruck(truckId).block();

        // then
        assertThat(haulCycle).hasValueSatisfying(c -> {
            assertThat(c.truckId()).isEqualTo(truckId);
            assertThat(c.insertTimestamp()).isNotNull();
        });
    }

    @Test
    void should_insert_and_get_last_complete_haul_cycle() {
        // when
        var truckId = UUID.randomUUID();
        var shovelId = UUID.randomUUID();
        sut.insert(truckId, shovelId, Instant.now(), Instant.now(), 41.1494512, -8.6107884, Instant.now(), 10, Instant.now(), Instant.now()).block();
        var haulCycle = sut.getLastHaulCycleForTruck(truckId).block();

        // then
        assertThat(haulCycle).hasValueSatisfying(c -> {
            assertThat(c.truckId()).isEqualTo(truckId);
            assertThat(c.insertTimestamp()).isNotNull();
            assertThat(c.shovelId()).isEqualTo(shovelId);
            assertThat(c.waitLoadTimestamp()).isNotNull();
            assertThat(c.startLoadTimestamp()).isNotNull();
            assertThat(c.startLoadLatitude()).isCloseTo(41.1494512, offset(1e-6));
            assertThat(c.startLoadLongitude()).isCloseTo(-8.6107884, offset(1e-6));
            assertThat(c.endLoadTimestamp()).isNotNull();
            assertThat(c.endLoadPayload()).isEqualTo(10);
            assertThat(c.startUnloadTimestamp()).isNotNull();
            assertThat(c.endUnloadTimestamp()).isNotNull();
        });
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
    }
}
