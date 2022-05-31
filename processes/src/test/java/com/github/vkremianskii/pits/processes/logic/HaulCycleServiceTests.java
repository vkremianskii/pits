package com.github.vkremianskii.pits.processes.logic;

import com.github.vkremianskii.pits.core.model.equipment.Truck;
import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.HaulCycleRepository;
import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsm;
import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsmFactory;
import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsmSink;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.processes.model.MutableHaulCycle;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static com.github.vkremianskii.pits.core.TestEquipment.aShovel;
import static com.github.vkremianskii.pits.core.TestEquipment.aTruck;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
        var truck = aTruck();
        when(haulCycleRepository.getLastHaulCycleForTruck(truck.id))
            .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getLastRecordForEquipmentBefore(truck.id, Instant.EPOCH))
            .thenReturn(Mono.just(Optional.empty()));
        when(positionRepository.getRecordsForEquipmentAfter(truck.id, Instant.EPOCH))
            .thenReturn(Mono.just(emptyList()));
        when(payloadRepository.getLastRecordForEquipmentBefore(truck.id, Instant.EPOCH))
            .thenReturn(Mono.just(Optional.empty()));
        when(payloadRepository.getRecordsForEquipmentAfter(truck.id, Instant.EPOCH))
            .thenReturn(Mono.just(emptyList()));

        // when
        sut.computeHaulCycles(truck, emptyList()).block();

        // then
        verify(haulCycleRepository).getLastHaulCycleForTruck(truck.id);
        verify(haulCycleFsmFactory).create(eq(emptyMap()), any());
        verify(haulCycleFsm).initialize(null, null, null, null);
        verify(haulCycleFsm).getTruckState();
        verifyNoMoreInteractions(haulCycleRepository);
        verifyNoMoreInteractions(haulCycleFsmFactory);
        verifyNoMoreInteractions(haulCycleFsm);
        verifyNoInteractions(registryClient);
    }

    @Test
    void should_compute_haul_cycles__complex_scenario() {
        // given
        var truck = aTruck();
        var shovel = aShovel();
        var haulCycle = new HaulCycle(
            1,
            truck.id,
            Instant.ofEpochSecond(2),
            shovel.id,
            Instant.ofEpochSecond(2),
            null,
            null,
            null,
            null,
            null,
            null,
            null);
        when(haulCycleRepository.getLastHaulCycleForTruck(truck.id))
            .thenReturn(Mono.just(Optional.of(haulCycle)));
        when(positionRepository.getLastRecordForEquipmentBefore(truck.id, Instant.ofEpochSecond(2)))
            .thenReturn(Mono.just(Optional.of(new EquipmentPositionRecord(1, truck.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)))));
        when(positionRepository.getRecordsForEquipmentAfter(truck.id, Instant.ofEpochSecond(2)))
            .thenReturn(Mono.just(List.of(
                new EquipmentPositionRecord(2, truck.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)),
                new EquipmentPositionRecord(3, truck.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(4))
            )));
        when(payloadRepository.getLastRecordForEquipmentBefore(truck.id, Instant.ofEpochSecond(2)))
            .thenReturn(Mono.just(Optional.of(new EquipmentPayloadRecord(1, truck.id, 0, Instant.ofEpochSecond(1)))));
        when(payloadRepository.getRecordsForEquipmentAfter(truck.id, Instant.ofEpochSecond(2)))
            .thenReturn(Mono.just(List.of(
                new EquipmentPayloadRecord(2, truck.id, 10_000, Instant.ofEpochSecond(3)),
                new EquipmentPayloadRecord(3, truck.id, 20_000, Instant.ofEpochSecond(4))
            )));
        when(positionRepository.getLastRecordForEquipmentBefore(shovel.id, Instant.ofEpochSecond(2)))
            .thenReturn(Mono.just(Optional.of(new EquipmentPositionRecord(4, shovel.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)))));
        when(positionRepository.getRecordsForEquipmentAfter(shovel.id, Instant.ofEpochSecond(2)))
            .thenReturn(Mono.just(List.of(
                new EquipmentPositionRecord(5, shovel.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)),
                new EquipmentPositionRecord(6, shovel.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(4))
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
                shovel.id,
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
        when(haulCycleRepository.update(eq(1L), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(Mono.empty());
        when(haulCycleRepository.insert(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(Mono.empty());
        when(haulCycleFsm.getTruckState()).thenReturn(Truck.STATE_LOAD);
        when(registryClient.updateEquipmentState(truck.id, Truck.STATE_LOAD)).thenReturn(Mono.empty());

        // when
        sut.computeHaulCycles(truck, List.of(shovel)).block();

        // then
        verify(haulCycleRepository).getLastHaulCycleForTruck(truck.id);
        verify(haulCycleFsmFactory).create(eq(Map.of(
            shovel, new TreeMap<>(Map.of(
                Instant.ofEpochSecond(1), new EquipmentPositionRecord(4, shovel.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(1)),
                Instant.ofEpochSecond(3), new EquipmentPositionRecord(5, shovel.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)),
                Instant.ofEpochSecond(4), new EquipmentPositionRecord(6, shovel.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(4))
            ))
        )), any());
        verify(haulCycleFsm).initialize(haulCycle, 41.1494512, -8.6107884, 0);
        verify(haulCycleFsm).consume(new EquipmentPositionRecord(2, truck.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(3)));
        verify(haulCycleFsm).consume(new EquipmentPayloadRecord(2, truck.id, 10_000, Instant.ofEpochSecond(3)));
        verify(haulCycleFsm).consume(new EquipmentPositionRecord(3, truck.id, 41.1494512, -8.6107884, 86, Instant.ofEpochSecond(4)));
        verify(haulCycleFsm).consume(new EquipmentPayloadRecord(3, truck.id, 20_000, Instant.ofEpochSecond(4)));
        verify(haulCycleFsm).getTruckState();
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
            truck.id,
            shovel.id,
            Instant.ofEpochSecond(6),
            null,
            null,
            null,
            null,
            null,
            null,
            null);
        verify(registryClient).updateEquipmentState(truck.id, Truck.STATE_LOAD);
        verifyNoMoreInteractions(haulCycleRepository);
        verifyNoMoreInteractions(haulCycleFsmFactory);
        verifyNoMoreInteractions(haulCycleFsm);
        verifyNoMoreInteractions(registryClient);
    }
}
