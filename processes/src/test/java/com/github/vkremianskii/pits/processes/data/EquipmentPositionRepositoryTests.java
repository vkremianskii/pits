package com.github.vkremianskii.pits.processes.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EquipmentPositionRepositoryTests {
    @Autowired
    EquipmentPositionRepository sut;

    @BeforeEach
    void cleanup() {
        sut.clear().block();
    }

    @Test
    void should_put_and_get_last_record() {
        // when
        sut.put(1, 41.1494512, -8.6107884, 86).block();

        // then
        var record = sut.getLastRecordByEquipmentId(1).block();
        assertThat(record).hasValueSatisfying(r -> {
            assertThat(r.equipmentId()).isEqualTo(1);
            assertThat(r.latitude()).isEqualTo(41.1494512);
            assertThat(r.longitude()).isEqualTo(-8.6107884);
            assertThat(r.elevation()).isEqualTo(86);
        });
    }
}
