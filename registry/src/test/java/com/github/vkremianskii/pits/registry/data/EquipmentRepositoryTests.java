package com.github.vkremianskii.pits.registry.data;

import com.github.vkremianskii.pits.registry.model.Position;
import com.github.vkremianskii.pits.registry.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.model.equipment.Truck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.registry.model.EquipmentType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
class EquipmentRepositoryTests {
    @Autowired
    EquipmentRepository sut;

    @BeforeEach
    void cleanup() {
        sut.clear().block();
    }

    @Test
    void should_put_and_get_equipment() {
        sut.put("Dozer No.1", DOZER).block();
        sut.put("Drill No.1", DRILL).block();
        sut.put("Shovel No.1", SHOVEL).block();
        sut.put("Truck No.1", TRUCK).block();

        var equipment = sut.getEquipment().block();

        assertThat(equipment).hasSize(4);
        assertThat(equipment).hasAtLeastOneElementOfType(Dozer.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Drill.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Shovel.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Truck.class);
    }

    @Test
    void should_update_equipment_position() {
        // given
        sut.put(1, "Truck No.1", TRUCK).block();

        // when
        sut.updateEquipmentPosition(1, new Position(41.1494512, -8.6107884, 86)).block();

        // then
        var equipment = sut.getEquipmentById(1).block();
        assertThat(equipment).isNotEmpty();
        assertThat(equipment).hasValueSatisfying(e -> {
            assertThat(e.getPosition()).isNotNull();
            assertThat(e.getPosition().getLatitude()).isCloseTo(41.1494512, offset(1e-8));
            assertThat(e.getPosition().getLongitude()).isCloseTo(-8.6107884, offset(1e-8));
            assertThat(e.getPosition().getElevation()).isEqualTo(86);
        });
    }
}
