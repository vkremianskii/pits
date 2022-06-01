package com.github.vkremianskii.pits.processes.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static com.github.vkremianskii.pits.registry.TestEquipment.randomEquipmentId;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EquipmentPositionRepositoryTests {

    @Autowired
    EquipmentPositionRepository sut;

    @Test
    void should_insert_and_get_last_record() {
        // when
        var equipmentId = randomEquipmentId();
        sut.insert(equipmentId, 41.1494512, -8.6107884, 86).block();
        var record = sut.getLastRecordForEquipment(equipmentId).block();

        // then
        assertThat(record).hasValueSatisfying(r -> {
            assertThat(r.equipmentId()).isEqualTo(equipmentId);
            assertThat(r.latitude()).isEqualTo(41.1494512);
            assertThat(r.longitude()).isEqualTo(-8.6107884);
            assertThat(r.elevation()).isEqualTo(86);
        });
    }

    @Test
    void should_insert_and_get_last_record_before() {
        // when
        var equipmentId = randomEquipmentId();
        sut.insert(equipmentId, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)).block();
        sut.insert(equipmentId, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(2)).block();
        sut.insert(equipmentId, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)).block();
        var record = sut.getLastRecordForEquipmentBefore(equipmentId, Instant.ofEpochSecond(2)).block();

        // then
        assertThat(record).hasValueSatisfying(r -> {
            assertThat(r.insertTimestamp()).isEqualTo(Instant.ofEpochSecond(2));
        });
    }

    @Test
    void should_insert_and_get_last_record_after() {
        // when
        var equipmentId = randomEquipmentId();
        sut.insert(equipmentId, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)).block();
        sut.insert(equipmentId, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(2)).block();
        sut.insert(equipmentId, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)).block();
        var records = sut.getRecordsForEquipmentAfter(equipmentId, Instant.ofEpochSecond(1)).block();

        // then
        assertThat(records).hasSize(2);
        assertThat(records.get(0).insertTimestamp()).isEqualTo(Instant.ofEpochSecond(2));
        assertThat(records.get(1).insertTimestamp()).isEqualTo(Instant.ofEpochSecond(3));
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
    }
}
