package com.github.vkremianskii.pits.processes.logic;

import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.HaulCycleRepository;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

public class HaulCycleServiceTests {
    HaulCycleRepository haulCycleRepository = mock(HaulCycleRepository.class);
    EquipmentPositionRepository positionRepository = mock(EquipmentPositionRepository.class);
    EquipmentPayloadRepository payloadRepository = mock(EquipmentPayloadRepository.class);
    RegistryClient registryClient = mock(RegistryClient.class);
    HaulCycleService sut = new HaulCycleService(
            haulCycleRepository,
            positionRepository,
            payloadRepository,
            registryClient);

    @Test
    void should_compute_haul_cycles__empty_db() {
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
        verify(positionRepository).getLastRecordForEquipmentBefore(1, Instant.EPOCH);
        verify(positionRepository).getRecordsForEquipmentAfter(1, Instant.EPOCH);
        verify(payloadRepository).getLastRecordForEquipmentBefore(1, Instant.EPOCH);
        verify(payloadRepository).getRecordsForEquipmentAfter(1, Instant.EPOCH);
        verifyNoMoreInteractions(haulCycleRepository);
        verifyNoMoreInteractions(positionRepository);
        verifyNoMoreInteractions(payloadRepository);
        verifyNoInteractions(registryClient);
    }

    @Test
    void should_compute_haul_cycles__cold_start() {
        // given
        var truck = new Truck(1, "Truck No.1", null, null, null);
        when(haulCycleRepository.getLastHaulCycleForTruck(1))
                .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getLastRecordForEquipmentBefore(1, Instant.EPOCH))
                .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getRecordsForEquipmentAfter(1, Instant.EPOCH))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPositionRecord(1, 1, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)),
                        new EquipmentPositionRecord(2, 1, 41.14807, -8.61107, 86, Instant.ofEpochSecond(5))
                )));
        when(payloadRepository.getLastRecordForEquipmentBefore(1, Instant.EPOCH))
                .thenReturn(Mono.just(Optional.empty()));
        when(payloadRepository.getRecordsForEquipmentAfter(1, Instant.EPOCH))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPayloadRecord(1, 1, 0, Instant.ofEpochSecond(2)),
                        new EquipmentPayloadRecord(2, 1, 15_000, Instant.ofEpochSecond(3)),
                        new EquipmentPayloadRecord(3, 1, 30_000, Instant.ofEpochSecond(4)),
                        new EquipmentPayloadRecord(4, 1, 15_000, Instant.ofEpochSecond(6)),
                        new EquipmentPayloadRecord(5, 1, 0, Instant.ofEpochSecond(7))
                )));
        when(registryClient.updateEquipmentState(eq(1), any())).thenReturn(Mono.empty());

        // when
        sut.computeHaulCycles(truck, emptyList()).block();

        // then
        verify(haulCycleRepository).getLastHaulCycleForTruck(1);
        verify(haulCycleRepository).insert(
                1,
                null,
                null,
                Instant.ofEpochSecond(3),
                41.1494512,
                -8.6107884,
                Instant.ofEpochSecond(5),
                30_000,
                Instant.ofEpochSecond(6),
                Instant.ofEpochSecond(7));
        verify(positionRepository).getLastRecordForEquipmentBefore(1, Instant.EPOCH);
        verify(positionRepository).getRecordsForEquipmentAfter(1, Instant.EPOCH);
        verify(payloadRepository).getLastRecordForEquipmentBefore(1, Instant.EPOCH);
        verify(payloadRepository).getRecordsForEquipmentAfter(1, Instant.EPOCH);
        verify(registryClient).updateEquipmentState(1, TruckState.EMPTY);
        verifyNoMoreInteractions(haulCycleRepository);
        verifyNoMoreInteractions(positionRepository);
        verifyNoMoreInteractions(payloadRepository);
        verifyNoMoreInteractions(registryClient);
    }

    @Test
    void should_compute_haul_cycles__from_existing() {
        // given
        var truck = new Truck(1, "Truck No.1", null, null, null);
        when(haulCycleRepository.getLastHaulCycleForTruck(1))
                .thenReturn(Mono.just(Optional.of(new HaulCycle(
                        1,
                        1,
                        Instant.ofEpochSecond(1),
                        2,
                        Instant.ofEpochSecond(1),
                        Instant.ofEpochSecond(2),
                        41.1494512,
                        -8.6107884,
                        Instant.ofEpochSecond(3),
                        30_000,
                        null,
                        null))));
        when(positionRepository.getLastRecordForEquipmentBefore(1, Instant.ofEpochSecond(1)))
                .thenReturn(Mono.just(Optional.of(
                        new EquipmentPositionRecord(1, 1, 41.14807, -8.61107, 86, Instant.ofEpochSecond(3))
                )));
        when(positionRepository.getRecordsForEquipmentAfter(1, Instant.ofEpochSecond(1)))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPositionRecord(2, 1, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(6)),
                        new EquipmentPositionRecord(3, 1, 41.14807, -8.61107, 86, Instant.ofEpochSecond(9))
                )));
        when(payloadRepository.getLastRecordForEquipmentBefore(1, Instant.ofEpochSecond(1)))
                .thenReturn(Mono.just(Optional.of(
                        new EquipmentPayloadRecord(1, 1, 30_000, Instant.ofEpochSecond(2))
                )));
        when(payloadRepository.getRecordsForEquipmentAfter(1, Instant.ofEpochSecond(1)))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPayloadRecord(2, 1, 15_000, Instant.ofEpochSecond(4)),
                        new EquipmentPayloadRecord(3, 1, 0, Instant.ofEpochSecond(5)),
                        new EquipmentPayloadRecord(4, 1, 15_000, Instant.ofEpochSecond(7)),
                        new EquipmentPayloadRecord(5, 1, 30_000, Instant.ofEpochSecond(8)),
                        new EquipmentPayloadRecord(6, 1, 15_000, Instant.ofEpochSecond(10))
                )));
        when(registryClient.updateEquipmentState(eq(1), any())).thenReturn(Mono.empty());

        // when
        sut.computeHaulCycles(truck, emptyList()).block();

        // then
        verify(haulCycleRepository).getLastHaulCycleForTruck(1);
        verify(haulCycleRepository).update(
                1,
                2,
                Instant.ofEpochSecond(1),
                Instant.ofEpochSecond(2),
                41.1494512,
                -8.6107884,
                Instant.ofEpochSecond(3),
                30_000,
                Instant.ofEpochSecond(4),
                Instant.ofEpochSecond(5));
        verify(haulCycleRepository).insert(
                1,
                null,
                null,
                Instant.ofEpochSecond(7),
                41.1494512,
                -8.6107884,
                Instant.ofEpochSecond(9),
                30_000,
                Instant.ofEpochSecond(10),
                null);
        verify(positionRepository).getLastRecordForEquipmentBefore(1, Instant.ofEpochSecond(1));
        verify(positionRepository).getRecordsForEquipmentAfter(1, Instant.ofEpochSecond(1));
        verify(payloadRepository).getLastRecordForEquipmentBefore(1, Instant.ofEpochSecond(1));
        verify(payloadRepository).getRecordsForEquipmentAfter(1, Instant.ofEpochSecond(1));
        verify(registryClient).updateEquipmentState(1, TruckState.UNLOAD);
        verifyNoMoreInteractions(haulCycleRepository);
        verifyNoMoreInteractions(positionRepository);
        verifyNoMoreInteractions(payloadRepository);
        verifyNoMoreInteractions(registryClient);
    }

    @Test
    void should_compute_haul_cycles__shovels() {
        // given
        var truck = new Truck(1, "Truck No.1", null, null, null);
        var shovel1 = new Shovel(2, "Shovel No.1", 20, null, null);
        var shovel2 = new Shovel(3, "Shovel No.2", 20, null, null);
        when(haulCycleRepository.getLastHaulCycleForTruck(1))
                .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getLastRecordForEquipmentBefore(1, Instant.EPOCH))
                .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getRecordsForEquipmentAfter(1, Instant.EPOCH))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPositionRecord(1, 1, 41.149654, -8.610744, 86, Instant.ofEpochSecond(1)),
                        new EquipmentPositionRecord(2, 1, 41.149343, -8.610256, 86, Instant.ofEpochSecond(3)),
                        new EquipmentPositionRecord(3, 1, 41.149654, -8.610744, 86, Instant.ofEpochSecond(5)),
                        new EquipmentPositionRecord(4, 1, 41.149116, -8.610846, 86, Instant.ofEpochSecond(8))
                )));
        when(payloadRepository.getLastRecordForEquipmentBefore(1, Instant.EPOCH))
                .thenReturn(Mono.just(Optional.empty()));
        when(payloadRepository.getRecordsForEquipmentAfter(1, Instant.EPOCH))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPayloadRecord(1, 1, 100_000, Instant.ofEpochSecond(4)),
                        new EquipmentPayloadRecord(2, 1, 50_000, Instant.ofEpochSecond(6)),
                        new EquipmentPayloadRecord(3, 1, 0, Instant.ofEpochSecond(7))
                )));
        when(positionRepository.getLastRecordForEquipmentBefore(2, Instant.EPOCH))
                .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getRecordsForEquipmentAfter(2, Instant.EPOCH))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPositionRecord(5, 2, 41.149320, -8.610143, 86, Instant.ofEpochSecond(2))
                )));
        when(positionRepository.getLastRecordForEquipmentBefore(3, Instant.EPOCH))
                .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getRecordsForEquipmentAfter(3, Instant.EPOCH))
                .thenReturn(Mono.just(List.of(
                        new EquipmentPositionRecord(6, 3, 41.149017, -8.610865, 86, Instant.ofEpochSecond(7))
                )));

        // when
        sut.computeHaulCycles(truck, List.of(shovel1, shovel2)).block();

        // then
        verify(haulCycleRepository).getLastHaulCycleForTruck(1);
        verify(haulCycleRepository).insert(
                1,
                2,
                Instant.ofEpochSecond(3),
                Instant.ofEpochSecond(4),
                41.149343,
                -8.610256,
                Instant.ofEpochSecond(5),
                100_000,
                Instant.ofEpochSecond(6),
                Instant.ofEpochSecond(7));
        verify(haulCycleRepository).insert(
                1,
                3,
                Instant.ofEpochSecond(8),
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        verify(registryClient).updateEquipmentState(1, TruckState.WAIT_LOAD);
        verifyNoMoreInteractions(haulCycleRepository);
        verifyNoMoreInteractions(registryClient);
    }
}
