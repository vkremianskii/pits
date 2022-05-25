package com.github.vkremianskii.pits.processes.logic;

import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.HaulCycleRepository;
import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsm;
import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsmFactory;
import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsmSink;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.*;

class HaulCycleServiceTests {
    HaulCycleRepository haulCycleRepository = mock(HaulCycleRepository.class);
    EquipmentPositionRepository positionRepository = mock(EquipmentPositionRepository.class);
    EquipmentPayloadRepository payloadRepository = mock(EquipmentPayloadRepository.class);
    RegistryClient registryClient = mock(RegistryClient.class);
    HaulCycleFsm haulCycleFsm = mock(HaulCycleFsm.class);
    HaulCycleFsmFactory haulCycleFsmFactory = mock(HaulCycleFsmFactory.class);
    HaulCycleService sut = new HaulCycleService(
            haulCycleRepository,
            positionRepository,
            payloadRepository,
            registryClient,
            haulCycleFsmFactory);

    @BeforeEach
    void setup() {
        when(haulCycleFsmFactory.create(any(), any())).thenReturn(haulCycleFsm);
    }

    @Test
    void should_compute_haul_cycles__no_data() {
        // given
        var truck = new Truck(1, "Truck No.1", null, null, null);
        when(haulCycleRepository.getLastHaulCycleForTruck(1))
                .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getLastRecordForEquipmentBefore(1, Instant.EPOCH))
                .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getRecordsForEquipmentAfter(1, Instant.EPOCH))
                .thenReturn(Mono.just(emptyList()));
        when(payloadRepository.getLastRecordForEquipmentBefore(1, Instant.EPOCH))
                .thenReturn(Mono.just(Optional.empty()));
        when(payloadRepository.getRecordsForEquipmentAfter(1, Instant.EPOCH))
                .thenReturn(Mono.just(emptyList()));

        // when
        sut.computeHaulCycles(truck, emptyList()).block();

        // then
        verify(haulCycleRepository).getLastHaulCycleForTruck(1);
        verify(haulCycleFsmFactory).create(eq(emptyMap()), any());
        verify(haulCycleFsm).initialize(null, null, null, null);
        verify(haulCycleFsm).getState();
        verifyNoMoreInteractions(haulCycleRepository);
        verifyNoMoreInteractions(haulCycleFsmFactory);
        verifyNoMoreInteractions(haulCycleFsm);
        verifyNoInteractions(registryClient);
    }

    @Test
    void should_compute_haul_cycles__complex_scenario() {
        // given
        var truck = new Truck(1, "Truck No.1", null, null, null);
        var shovel = new Shovel(2, "Shovel No.1", 20, null, null);
        var haulCycle = new HaulCycle(
                1,
                1,
                Instant.ofEpochSecond(2),
                2,
                Instant.ofEpochSecond(2),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        when(haulCycleRepository.getLastHaulCycleForTruck(1))
                .thenReturn(Mono.just(Optional.of(haulCycle)));
        when(positionRepository.getLastRecordForEquipmentBefore(1, Instant.ofEpochSecond(2)))
                .thenReturn(Mono.just(Optional.of(new EquipmentPositionRecord(1, 1, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)))));
        when(positionRepository.getRecordsForEquipmentAfter(1, Instant.ofEpochSecond(2)))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPositionRecord(2, 1, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)),
                        new EquipmentPositionRecord(3, 1, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(4))
                )));
        when(payloadRepository.getLastRecordForEquipmentBefore(1, Instant.ofEpochSecond(2)))
                .thenReturn(Mono.just(Optional.of(new EquipmentPayloadRecord(1, 1, 0, Instant.ofEpochSecond(1)))));
        when(payloadRepository.getRecordsForEquipmentAfter(1, Instant.ofEpochSecond(2)))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPayloadRecord(2, 1, 10_000, Instant.ofEpochSecond(3)),
                        new EquipmentPayloadRecord(3, 1, 20_000, Instant.ofEpochSecond(4))
                )));
        when(positionRepository.getLastRecordForEquipmentBefore(2, Instant.ofEpochSecond(2)))
                .thenReturn(Mono.just(Optional.of(new EquipmentPositionRecord(4, 2, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)))));
        when(positionRepository.getRecordsForEquipmentAfter(2, Instant.ofEpochSecond(2)))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPositionRecord(5, 2, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)),
                        new EquipmentPositionRecord(6, 2, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(4))
                )));
        when(haulCycleFsmFactory.create(any(), any())).then(invocation -> {
            var sink = invocation.getArgument(1, HaulCycleFsmSink.class);
            sink.append(new MutableHaulCycle(
                    1L,
                    null,
                    null,
                    Instant.ofEpochSecond(2),
                    41.1494512,
                    -8.6107884,
                    Instant.ofEpochSecond(3),
                    20_000,
                    Instant.ofEpochSecond(4),
                    Instant.ofEpochSecond(5)));
            sink.append(new MutableHaulCycle(
                    null,
                    2,
                    Instant.ofEpochSecond(6),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null));
            return haulCycleFsm;
        });
        when(haulCycleFsm.getState()).thenReturn(TruckState.LOAD);
        when(registryClient.updateEquipmentState(1, TruckState.LOAD)).thenReturn(Mono.empty());

        // when
        sut.computeHaulCycles(truck, List.of(shovel)).block();

        // then
        verify(haulCycleRepository).getLastHaulCycleForTruck(1);
        verify(haulCycleFsmFactory).create(eq(Map.of(
                shovel, new TreeMap<>(Map.of(
                        Instant.ofEpochSecond(1), new EquipmentPositionRecord(4, 2, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)),
                        Instant.ofEpochSecond(3), new EquipmentPositionRecord(5, 2, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)),
                        Instant.ofEpochSecond(4), new EquipmentPositionRecord(6, 2, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(4))
                ))
        )), any());
        verify(haulCycleFsm).initialize(haulCycle, 41.1494512, -8.6107884, 0);
        verify(haulCycleFsm).consume(new EquipmentPositionRecord(2, 1, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)));
        verify(haulCycleFsm).consume(new EquipmentPayloadRecord(2, 1, 10_000, Instant.ofEpochSecond(3)));
        verify(haulCycleFsm).consume(new EquipmentPositionRecord(3, 1, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(4)));
        verify(haulCycleFsm).consume(new EquipmentPayloadRecord(3, 1, 20_000, Instant.ofEpochSecond(4)));
        verify(haulCycleFsm).getState();
        verify(haulCycleRepository).update(
                1,
                null,
                null,
                Instant.ofEpochSecond(2),
                41.1494512,
                -8.6107884,
                Instant.ofEpochSecond(3),
                20_000,
                Instant.ofEpochSecond(4),
                Instant.ofEpochSecond(5));
        verify(haulCycleRepository).insert(
                1,
                2,
                Instant.ofEpochSecond(6),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        verify(registryClient).updateEquipmentState(1, TruckState.LOAD);
        verifyNoMoreInteractions(haulCycleRepository);
        verifyNoMoreInteractions(haulCycleFsmFactory);
        verifyNoMoreInteractions(haulCycleFsm);
        verifyNoMoreInteractions(registryClient);
    }
}
