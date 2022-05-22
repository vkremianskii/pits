package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.*;
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
    void should_insert_and_get_equipment() {
        // when
        sut.insert("Dozer No.1", DOZER, null).block();
        sut.insert("Drill No.1", DRILL, null).block();
        sut.insert("Shovel No.1", SHOVEL, (short) 20).block();
        sut.insert("Truck No.1", TRUCK, null).block();
        var equipment = sut.getEquipment().block();

        // then
        assertThat(equipment).hasSize(4);
        assertThat(equipment).hasAtLeastOneElementOfType(Dozer.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Drill.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Shovel.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Truck.class);
    }

    @Test
    void should_set_equipment_state() {
        // given
        sut.insert(1, "Truck No.1", TRUCK, null).block();
        sut.insert(2, "Truck No.2", TRUCK, null).block();
        sut.insert(3, "Truck No.3", TRUCK, null).block();
        sut.insert(4, "Truck No.4", TRUCK, null).block();

        // when
        sut.setEquipmentState(1, TruckState.EMPTY).block();
        sut.setEquipmentState(2, TruckState.LOAD).block();
        sut.setEquipmentState(3, TruckState.HAUL).block();
        sut.setEquipmentState(4, TruckState.UNLOAD).block();

        // then
        var truck1 = sut.getEquipmentById(1).block();
        var truck2 = sut.getEquipmentById(2).block();
        var truck3 = sut.getEquipmentById(3).block();
        var truck4 = sut.getEquipmentById(4).block();
        assertThat(truck1).hasValueSatisfying(t ->
        {
            assertThat(t.getState()).isEqualTo(TruckState.EMPTY);
        });
        assertThat(truck2).hasValueSatisfying(t ->
        {
            assertThat(t.getState()).isEqualTo(TruckState.LOAD);
        });
        assertThat(truck3).hasValueSatisfying(t ->
        {
            assertThat(t.getState()).isEqualTo(TruckState.HAUL);
        });
        assertThat(truck4).hasValueSatisfying(t ->
        {
            assertThat(t.getState()).isEqualTo(TruckState.UNLOAD);
        });
    }

    @Test
    void should_set_equipment_position() {
        // given
        sut.insert(1, "Truck No.1", TRUCK, null).block();

        // when
        sut.setEquipmentPosition(1, new Position(41.1494512, -8.6107884, 86)).block();

        // then
        var equipment = sut.getEquipmentById(1).block();
        assertThat(equipment).hasValueSatisfying(t -> {
            assertThat(t.getPosition()).isNotNull();
            assertThat(t.getPosition().getLatitude()).isCloseTo(41.1494512, offset(1e-8));
            assertThat(t.getPosition().getLongitude()).isCloseTo(-8.6107884, offset(1e-8));
            assertThat(t.getPosition().getElevation()).isEqualTo(86);
        });
    }

    @Test
    void should_set_equipment_payload() {
        // given
        sut.insert(1, "Truck No.1", TRUCK, null).block();

        // when
        sut.setEquipmentPayload(1, 10).block();

        // then
        var equipment = sut.getEquipmentById(1).block();
        assertThat(equipment).hasValueSatisfying(t -> {
            assertThat(t).isInstanceOf(Truck.class);
            assertThat(((Truck) t).getPayload()).isEqualTo(10);
        });
    }
}
