package com.github.vkremianskii.pits.processes.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EquipmentPayloadRepositoryTests {
    @Autowired
    EquipmentPayloadRepository sut;

    @BeforeEach
    void cleanup() {
        sut.clear().block();
    }

    @Test
    void should_insert_and_get_last_record() {
        // when
        sut.insert(1, 10).block();
        var record = sut.getLastRecordForEquipment(1).block();

        // then
        assertThat(record).hasValueSatisfying(r -> {
            assertThat(r.equipmentId()).isEqualTo(1);
            assertThat(r.payload()).isEqualTo(10);
        });
    }

    @Test
    void should_insert_and_get_last_record_before() {
        // when
        sut.insert(1, 5, Instant.ofEpochSecond(1)).block();
        sut.insert(1, 10, Instant.ofEpochSecond(2)).block();
        sut.insert(1, 15, Instant.ofEpochSecond(3)).block();
        var record = sut.getLastRecordForEquipmentBefore(1, Instant.ofEpochSecond(2)).block();

        // then
        assertThat(record).hasValueSatisfying(r -> {
            assertThat(r.equipmentId()).isEqualTo(1);
            assertThat(r.payload()).isEqualTo(10);
        });
    }

    @Test
    void should_insert_and_get_records_after() {
        // when
        sut.insert(1, 5, Instant.ofEpochSecond(1)).block();
        sut.insert(1, 10, Instant.ofEpochSecond(2)).block();
        sut.insert(1, 15, Instant.ofEpochSecond(3)).block();
        var records = sut.getRecordsForEquipmentAfter(1, Instant.ofEpochSecond(1)).block();

        // then
        assertThat(records).hasSize(2);
        assertThat(records.get(0).payload()).isEqualTo(10);
        assertThat(records.get(1).payload()).isEqualTo(15);
    }
}
