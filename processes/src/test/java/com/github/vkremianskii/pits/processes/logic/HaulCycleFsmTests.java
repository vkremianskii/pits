package com.github.vkremianskii.pits.processes.logic;

import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsm;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.processes.model.MutableHaulCycle;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class HaulCycleFsmTests {

    @Test
    void should_compute_haul_cycles__no_data() {
        // given
        var shovelToOrderedPositions = new HashMap<Shovel, SortedMap<Instant, EquipmentPositionRecord>>();
        var haulCycles = new ArrayList<MutableHaulCycle>();
        var haulCycleFsm = new HaulCycleFsm(shovelToOrderedPositions, haulCycles::add);
        haulCycleFsm.initialize(null, null, null, null);

        // when

        // then
        assertThat(haulCycles).isEmpty();
        var state = haulCycleFsm.getState();
        assertThat(state).isNull();
    }

    @Test
    void should_compute_haul_cycles__cold_start() {
        // given
        var truckId = UUID.randomUUID();
        var shovelToOrderedPositions = new HashMap<Shovel, SortedMap<Instant, EquipmentPositionRecord>>();
        var haulCycles = new ArrayList<MutableHaulCycle>();
        var haulCycleFsm = new HaulCycleFsm(shovelToOrderedPositions, haulCycles::add);
        haulCycleFsm.initialize(null, null, null, null);

        // when
        haulCycleFsm.consume(new EquipmentPositionRecord(1, truckId, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(1, truckId, 0, Instant.ofEpochSecond(2)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(2, truckId, 15_000, Instant.ofEpochSecond(3)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(3, truckId, 30_000, Instant.ofEpochSecond(4)));
        haulCycleFsm.consume(new EquipmentPositionRecord(2, truckId, 41.14807, -8.61107, 86, Instant.ofEpochSecond(5)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(4, truckId, 15_000, Instant.ofEpochSecond(6)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(5, truckId, 0, Instant.ofEpochSecond(7)));

        // then
        assertThat(haulCycles).containsExactly(new MutableHaulCycle(
            null,
            null,
            null,
            Instant.ofEpochSecond(3),
            41.1494512,
            -8.6107884,
            Instant.ofEpochSecond(5),
            30_000,
            Instant.ofEpochSecond(6),
            Instant.ofEpochSecond(7)));
        var state = haulCycleFsm.getState();
        assertThat(state).isEqualTo(TruckState.EMPTY);
    }

    @Test
    void should_compute_haul_cycles__from_existing() {
        // given
        var truckId = UUID.randomUUID();
        var shovelId = UUID.randomUUID();
        var shovelToOrderedPositions = new HashMap<Shovel, SortedMap<Instant, EquipmentPositionRecord>>();
        var haulCycles = new ArrayList<MutableHaulCycle>();
        var haulCycleFsm = new HaulCycleFsm(shovelToOrderedPositions, haulCycles::add);
        var haulCycle = new HaulCycle(
            1L,
            truckId,
            Instant.ofEpochSecond(1),
            shovelId,
            Instant.ofEpochSecond(1),
            Instant.ofEpochSecond(2),
            41.1494512,
            -8.6107884,
            Instant.ofEpochSecond(3),
            30_000,
            null,
            null);
        haulCycleFsm.initialize(haulCycle, 41.14807, -8.61107, 30_000);

        // when
        haulCycleFsm.consume(new EquipmentPayloadRecord(2, truckId, 15_000, Instant.ofEpochSecond(4)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(3, truckId, 0, Instant.ofEpochSecond(5)));
        haulCycleFsm.consume(new EquipmentPositionRecord(2, truckId, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(6)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(4, truckId, 15_000, Instant.ofEpochSecond(7)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(5, truckId, 30_000, Instant.ofEpochSecond(8)));
        haulCycleFsm.consume(new EquipmentPositionRecord(3, truckId, 41.14807, -8.61107, 86, Instant.ofEpochSecond(9)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(6, truckId, 15_000, Instant.ofEpochSecond(10)));

        // then
        assertThat(haulCycles).containsExactlyInAnyOrder(
            new MutableHaulCycle(
                1L,
                shovelId,
                Instant.ofEpochSecond(1),
                Instant.ofEpochSecond(2),
                41.1494512,
                -8.6107884,
                Instant.ofEpochSecond(3),
                30_000,
                Instant.ofEpochSecond(4),
                Instant.ofEpochSecond(5)),
            new MutableHaulCycle(
                null,
                null,
                null,
                Instant.ofEpochSecond(7),
                41.1494512,
                -8.6107884,
                Instant.ofEpochSecond(9),
                30_000,
                Instant.ofEpochSecond(10),
                null));
        var state = haulCycleFsm.getState();
        assertThat(state).isEqualTo(TruckState.UNLOAD);
    }

    @Test
    void should_compute_haul_cycles__shovels() {
        // given
        var truckId = UUID.randomUUID();
        var shovel1Id = UUID.randomUUID();
        var shovel2Id = UUID.randomUUID();
        var shovel1 = new Shovel(shovel1Id, "Shovel No.1", 20, null, null);
        var shovel2 = new Shovel(shovel2Id, "Shovel No.2", 20, null, null);
        var shovelToOrderedPositions = Map.of(
            shovel1, new TreeMap<>(Map.of(
                Instant.ofEpochSecond(2),
                new EquipmentPositionRecord(5, shovel1Id, 41.149320, -8.610143, 86, Instant.ofEpochSecond(2))
            )),
            shovel2, new TreeMap<>(Map.of(
                Instant.ofEpochSecond(7),
                new EquipmentPositionRecord(6, shovel2Id, 41.149017, -8.610865, 86, Instant.ofEpochSecond(7))
            )));
        var haulCycles = new ArrayList<MutableHaulCycle>();
        var haulCycleFsm = new HaulCycleFsm(shovelToOrderedPositions, haulCycles::add);
        haulCycleFsm.initialize(null, 41.14807, -8.61107, 30_000);

        // when
        haulCycleFsm.consume(new EquipmentPositionRecord(1, truckId, 41.149654, -8.610744, 86, Instant.ofEpochSecond(1)));
        haulCycleFsm.consume(new EquipmentPositionRecord(2, truckId, 41.149343, -8.610256, 86, Instant.ofEpochSecond(3)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(1, truckId, 100_000, Instant.ofEpochSecond(4)));
        haulCycleFsm.consume(new EquipmentPositionRecord(3, truckId, 41.149654, -8.610744, 86, Instant.ofEpochSecond(5)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(2, truckId, 50_000, Instant.ofEpochSecond(6)));
        haulCycleFsm.consume(new EquipmentPayloadRecord(3, truckId, 0, Instant.ofEpochSecond(7)));
        haulCycleFsm.consume(new EquipmentPositionRecord(4, truckId, 41.149116, -8.610846, 86, Instant.ofEpochSecond(8)));

        // then
        assertThat(haulCycles).containsExactlyInAnyOrder(
            new MutableHaulCycle(
                null,
                shovel1Id,
                Instant.ofEpochSecond(3),
                Instant.ofEpochSecond(4),
                41.149343,
                -8.610256,
                Instant.ofEpochSecond(5),
                100_000,
                Instant.ofEpochSecond(6),
                Instant.ofEpochSecond(7)),
            new MutableHaulCycle(
                null,
                shovel2Id,
                Instant.ofEpochSecond(8),
                null,
                null,
                null,
                null,
                null,
                null,
                null));
        var state = haulCycleFsm.getState();
        assertThat(state).isEqualTo(TruckState.WAIT_LOAD);
    }
}
