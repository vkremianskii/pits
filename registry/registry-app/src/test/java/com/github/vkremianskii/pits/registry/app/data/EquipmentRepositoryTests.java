package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.types.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.EquipmentId.equipmentId;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
class EquipmentRepositoryTests {

    @Autowired
    EquipmentRepository sut;

    @Test
    void should_create_and_get_equipment() {
        // when
        var dozerId = equipmentId(UUID.randomUUID());
        sut.createEquipment(dozerId, "Dozer No.1", DOZER, null).block();
        var drillId = equipmentId(UUID.randomUUID());
        sut.createEquipment(drillId, "Drill No.1", DRILL, null).block();
        var shovelId = equipmentId(UUID.randomUUID());
        sut.createEquipment(shovelId, "Shovel No.1", SHOVEL, (short) 20).block();
        var truckId = equipmentId(UUID.randomUUID());
        sut.createEquipment(truckId, "Truck No.1", TRUCK, null).block();
        var equipment = sut.getEquipment().block();

        // then
        var equipmentById = equipment.stream().collect(toMap(e -> e.id, identity()));
        var dozer = equipmentById.get(dozerId);
        assertThat(dozer).isInstanceOf(Dozer.class);
        assertThat(dozer.name).isEqualTo("Dozer No.1");
        assertThat(dozer.type).isEqualTo(DOZER);
        var drill = equipmentById.get(drillId);
        assertThat(drill).isInstanceOf(Drill.class);
        assertThat(drill.name).isEqualTo("Drill No.1");
        assertThat(drill.type).isEqualTo(DRILL);
        var shovel = equipmentById.get(shovelId);
        assertThat(shovel).isInstanceOf(Shovel.class);
        assertThat(shovel.name).isEqualTo("Shovel No.1");
        assertThat(shovel.type).isEqualTo(SHOVEL);
        var truck = equipmentById.get(truckId);
        assertThat(truck).isInstanceOf(Truck.class);
        assertThat(truck.name).isEqualTo("Truck No.1");
        assertThat(truck.type).isEqualTo(TRUCK);
    }

    @Test
    void should_get_equipment_by_id() {
        // given
        var truckId = equipmentId(UUID.randomUUID());
        sut.createEquipment(truckId, "Truck No.1", TRUCK, null).block();

        // when
        var truck = sut.getEquipmentById(truckId).block();

        // then
        assertThat(truck).hasValueSatisfying(t -> {
            assertThat(t).isInstanceOf(Truck.class);
            assertThat(t.id).isEqualTo(truckId);
            assertThat(t.name).isEqualTo("Truck No.1");
            assertThat(t.type).isEqualTo(TRUCK);
        });
    }

    @Test
    void should_update_equipment_state() {
        // given
        var truck1Id = equipmentId(UUID.randomUUID());
        sut.createEquipment(truck1Id, "Truck No.1", TRUCK, null).block();
        var truck2Id = equipmentId(UUID.randomUUID());
        sut.createEquipment(truck2Id, "Truck No.2", TRUCK, null).block();
        var truck3Id = equipmentId(UUID.randomUUID());
        sut.createEquipment(truck3Id, "Truck No.3", TRUCK, null).block();
        var truck4Id = equipmentId(UUID.randomUUID());
        sut.createEquipment(truck4Id, "Truck No.4", TRUCK, null).block();

        // when
        sut.updateEquipmentState(truck1Id, TruckState.EMPTY).block();
        sut.updateEquipmentState(truck2Id, TruckState.LOAD).block();
        sut.updateEquipmentState(truck3Id, TruckState.HAUL).block();
        sut.updateEquipmentState(truck4Id, TruckState.UNLOAD).block();

        // then
        var equipment = sut.getEquipment().block();
        var equipmentById = equipment.stream().collect(toMap(e -> e.id, identity()));
        var truck1 = equipmentById.get(truck1Id);
        var truck2 = equipmentById.get(truck2Id);
        var truck3 = equipmentById.get(truck3Id);
        var truck4 = equipmentById.get(truck4Id);
        assertThat(truck1.state).isEqualTo(TruckState.EMPTY);
        assertThat(truck2.state).isEqualTo(TruckState.LOAD);
        assertThat(truck3.state).isEqualTo(TruckState.HAUL);
        assertThat(truck4.state).isEqualTo(TruckState.UNLOAD);
    }

    @Test
    void should_update_equipment_position() {
        // given
        var truckId = equipmentId(UUID.randomUUID());
        sut.createEquipment(truckId, "Truck No.1", TRUCK, null).block();

        // when
        sut.updateEquipmentPosition(truckId, new Position(41.1494512, -8.6107884, 86)).block();

        // then
        var truck = sut.getEquipmentById(truckId).block();
        assertThat(truck).hasValueSatisfying(t -> {
            assertThat(t.position).isNotNull();
            assertThat(t.position.latitude()).isCloseTo(41.1494512, offset(1e-8));
            assertThat(t.position.longitude()).isCloseTo(-8.6107884, offset(1e-8));
            assertThat(t.position.elevation()).isEqualTo(86);
        });
    }

    @Test
    void should_update_equipment_payload() {
        // given
        var truckId = equipmentId(UUID.randomUUID());
        sut.createEquipment(truckId, "Truck No.1", TRUCK, null).block();

        // when
        sut.updateEquipmentPayload(truckId, 10).block();

        // then
        var truck = sut.getEquipmentById(truckId).block();
        assertThat(truck).hasValueSatisfying(t -> {
            assertThat(t).isInstanceOf(Truck.class);
            assertThat(((Truck) t).payload).isEqualTo(10);
        });
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
    }
}
