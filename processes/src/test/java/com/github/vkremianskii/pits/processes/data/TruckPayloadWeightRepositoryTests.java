package com.github.vkremianskii.pits.processes.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TruckPayloadWeightRepositoryTests {
    @Autowired
    TruckPayloadWeightRepository sut;

    @BeforeEach
    void cleanup() {
        sut.clear().block();
    }

    @Test
    void should_put_and_get_record() {
        // when
        sut.put(1, 10).block();

        // then
        var record = sut.getLastRecordByEquipmentId(1).block();
        assertThat(record).hasValueSatisfying(r -> {
            assertThat(r.equipmentId()).isEqualTo(1);
            assertThat(r.weight()).isEqualTo(10);
        });
    }
}
