package com.github.vkremianskii.pits.registry.data;

import com.github.vkremianskii.pits.registry.model.Position;
import com.github.vkremianskii.pits.registry.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.model.equipment.Truck;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.registry.TestEquipment.randomEquipmentId;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.TRUCK;
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
        var dozerId = randomEquipmentId();
        sut.createEquipment(dozerId, "Dozer No.1", DOZER, null).block();
        var drillId = randomEquipmentId();
        sut.createEquipment(drillId, "Drill No.1", DRILL, null).block();
        var shovelId = randomEquipmentId();
        sut.createEquipment(shovelId, "Shovel No.1", SHOVEL, (short) 20).block();
        var truckId = randomEquipmentId();
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
        var truckId = randomEquipmentId();
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
        var truck1Id = randomEquipmentId();
        sut.createEquipment(truck1Id, "Truck No.1", TRUCK, null).block();
        var truck2Id = randomEquipmentId();
        sut.createEquipment(truck2Id, "Truck No.2", TRUCK, null).block();
        var truck3Id = randomEquipmentId();
        sut.createEquipment(truck3Id, "Truck No.3", TRUCK, null).block();
        var truck4Id = randomEquipmentId();
        sut.createEquipment(truck4Id, "Truck No.4", TRUCK, null).block();

        // when
        sut.updateEquipmentState(truck1Id, Truck.STATE_EMPTY).block();
        sut.updateEquipmentState(truck2Id, Truck.STATE_LOAD).block();
        sut.updateEquipmentState(truck3Id, Truck.STATE_HAUL).block();
        sut.updateEquipmentState(truck4Id, Truck.STATE_UNLOAD).block();

        // then
        var equipment = sut.getEquipment().block();
        var equipmentById = equipment.stream().collect(toMap(e -> e.id, identity()));
        var truck1 = equipmentById.get(truck1Id);
        var truck2 = equipmentById.get(truck2Id);
        var truck3 = equipmentById.get(truck3Id);
        var truck4 = equipmentById.get(truck4Id);
        assertThat(truck1.state).isEqualTo(Truck.STATE_EMPTY);
        assertThat(truck2.state).isEqualTo(Truck.STATE_LOAD);
        assertThat(truck3.state).isEqualTo(Truck.STATE_HAUL);
        assertThat(truck4.state).isEqualTo(Truck.STATE_UNLOAD);
    }

    @Test
    void should_update_equipment_position() {
        // given
        var truckId = randomEquipmentId();
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
        var truckId = randomEquipmentId();
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
