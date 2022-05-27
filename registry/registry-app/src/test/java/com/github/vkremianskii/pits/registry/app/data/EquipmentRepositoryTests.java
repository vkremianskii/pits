package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
    void should_update_equipment_state() {
        // given
        sut.insert(1, "Truck No.1", TRUCK, null).block();
        sut.insert(2, "Truck No.2", TRUCK, null).block();
        sut.insert(3, "Truck No.3", TRUCK, null).block();
        sut.insert(4, "Truck No.4", TRUCK, null).block();

        // when
        sut.updateEquipmentState(1, TruckState.EMPTY).block();
        sut.updateEquipmentState(2, TruckState.LOAD).block();
        sut.updateEquipmentState(3, TruckState.HAUL).block();
        sut.updateEquipmentState(4, TruckState.UNLOAD).block();

        // then
        var truck1 = sut.getEquipmentById(1).block();
        var truck2 = sut.getEquipmentById(2).block();
        var truck3 = sut.getEquipmentById(3).block();
        var truck4 = sut.getEquipmentById(4).block();
        assertThat(truck1).hasValueSatisfying(t ->
        {
            assertThat(t.state).isEqualTo(TruckState.EMPTY);
        });
        assertThat(truck2).hasValueSatisfying(t ->
        {
            assertThat(t.state).isEqualTo(TruckState.LOAD);
        });
        assertThat(truck3).hasValueSatisfying(t ->
        {
            assertThat(t.state).isEqualTo(TruckState.HAUL);
        });
        assertThat(truck4).hasValueSatisfying(t ->
        {
            assertThat(t.state).isEqualTo(TruckState.UNLOAD);
        });
    }

    @Test
    @Disabled("id returns null using H2 database")
    void should_create_equipment() {
        // when
        sut.createEquipment("Truck No.1", TRUCK).block();

        // then
        var equipment = sut.getEquipment().block();
        assertThat(equipment).hasSize(1);
        var truck = equipment.get(0);
        assertThat(truck.name).isEqualTo("Truck No.1");
        assertThat(truck.type).isEqualTo(TRUCK);
    }

    @Test
    void should_update_equipment_position() {
        // given
        sut.insert(1, "Truck No.1", TRUCK, null).block();

        // when
        sut.updateEquipmentPosition(1, new Position(41.1494512, -8.6107884, 86)).block();

        // then
        var equipment = sut.getEquipmentById(1).block();
        assertThat(equipment).hasValueSatisfying(t -> {
            assertThat(t.position).isNotNull();
            assertThat(t.position.latitude()).isCloseTo(41.1494512, offset(1e-8));
            assertThat(t.position.longitude()).isCloseTo(-8.6107884, offset(1e-8));
            assertThat(t.position.elevation()).isEqualTo(86);
        });
    }

    @Test
    void should_update_equipment_payload() {
        // given
        sut.insert(1, "Truck No.1", TRUCK, null).block();

        // when
        sut.updateEquipmentPayload(1, 10).block();

        // then
        var equipment = sut.getEquipmentById(1).block();
        assertThat(equipment).hasValueSatisfying(t -> {
            assertThat(t).isInstanceOf(Truck.class);
            assertThat(((Truck) t).payload).isEqualTo(10);
        });
    }
}
