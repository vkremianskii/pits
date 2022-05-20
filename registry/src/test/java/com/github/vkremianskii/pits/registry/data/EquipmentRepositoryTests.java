package com.github.vkremianskii.pits.registry.data;

import com.github.vkremianskii.pits.registry.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.model.equipment.Truck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

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
        sut.put(new Dozer(1, "Dozer No.1", null)).block();
        sut.put(new Drill(2, "Drill No.1", null)).block();
        sut.put(new Shovel(3, "Shovel No.1", null)).block();
        sut.put(new Truck(4, "Truck No.1", null)).block();

        var equipment = sut.getEquipment().block();

        assertThat(equipment).hasSize(4);
        assertThat(equipment).hasAtLeastOneElementOfType(Dozer.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Drill.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Shovel.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Truck.class);
    }
}
