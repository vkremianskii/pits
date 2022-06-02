package com.github.vkremianskii.pits.processes.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.registry.TestEquipment.randomEquipmentId;
import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
class HaulCycleRepositoryTests {

    @Autowired
    HaulCycleRepository sut;

    @Test
    void should_insert_and_get_last_minimal_haul_cycle() {
        // when
        var truckId = randomEquipmentId();
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
        var truckId = randomEquipmentId();
        var shovelId = randomEquipmentId();
        sut.insert(truckId, shovelId, now(), now(), 41.1494512, -8.6107884, now(), 10, now(), now()).block();
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
