package com.github.vkremianskii.pits.processes.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.EquipmentId.equipmentId;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EquipmentPayloadRepositoryTests {

    @Autowired
    EquipmentPayloadRepository sut;

    @Test
    void should_insert_and_get_last_record() {
        // when
        var equipmentId = equipmentId(UUID.randomUUID());
        sut.insert(equipmentId, 10).block();
        var record = sut.getLastRecordForEquipment(equipmentId).block();

        // then
        assertThat(record).hasValueSatisfying(r -> {
            assertThat(r.equipmentId()).isEqualTo(equipmentId);
            assertThat(r.payload()).isEqualTo(10);
        });
    }

    @Test
    void should_insert_and_get_last_record_before() {
        // when
        var equipmentId = equipmentId(UUID.randomUUID());
        sut.insert(equipmentId, 5, Instant.ofEpochSecond(1)).block();
        sut.insert(equipmentId, 10, Instant.ofEpochSecond(2)).block();
        sut.insert(equipmentId, 15, Instant.ofEpochSecond(3)).block();
        var record = sut.getLastRecordForEquipmentBefore(equipmentId, Instant.ofEpochSecond(2)).block();

        // then
        assertThat(record).hasValueSatisfying(r -> {
            assertThat(r.equipmentId()).isEqualTo(equipmentId);
            assertThat(r.payload()).isEqualTo(10);
        });
    }

    @Test
    void should_insert_and_get_records_after() {
        // when
        var equipmentId = equipmentId(UUID.randomUUID());
        sut.insert(equipmentId, 5, Instant.ofEpochSecond(1)).block();
        sut.insert(equipmentId, 10, Instant.ofEpochSecond(2)).block();
        sut.insert(equipmentId, 15, Instant.ofEpochSecond(3)).block();
        var records = sut.getRecordsForEquipmentAfter(equipmentId, Instant.ofEpochSecond(1)).block();

        // then
        assertThat(records).hasSize(2);
        assertThat(records.get(0).payload()).isEqualTo(10);
        assertThat(records.get(1).payload()).isEqualTo(15);
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
    }
}
