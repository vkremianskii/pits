package com.github.vkremianskii.pits.processes.job;

import com.github.vkremianskii.pits.processes.logic.HaulCycleService;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentResponse;
import com.github.vkremianskii.pits.registry.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class HaulCycleJobTests {

    RegistryClient registryClient = mock(RegistryClient.class);
    HaulCycleService haulCycleService = mock(HaulCycleService.class);
    PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
    HaulCycleJob sut = new HaulCycleJob(registryClient, haulCycleService, transactionManager);

    @Test
    void should_compute_haul_cycles_for_all_trucks() {
        // given
        var dozerId = UUID.randomUUID();
        var shovelId = UUID.randomUUID();
        var truck1Id = UUID.randomUUID();
        var truck2Id = UUID.randomUUID();
        var dozer = new Dozer(dozerId, "Dozer No.1", null, null);
        var shovel = new Shovel(shovelId, "Shovel No.1", 20, null, null);
        var truck1 = new Truck(truck1Id, "Truck No.1", null, null, null);
        var truck2 = new Truck(truck2Id, "Truck No.2", null, null, null);
        when(registryClient.getEquipment()).thenReturn(Mono.just(new EquipmentResponse(List.of(dozer, shovel, truck1, truck2))));
        when(haulCycleService.computeHaulCycles(any(), any())).thenReturn(Mono.empty());

        // when
        sut.computeHaulCycles();

        // then
        verify(haulCycleService).computeHaulCycles(
            same(truck1),
            argThat(shovels -> shovels.size() == 1 && shovels.get(0).id == shovelId));
        verify(haulCycleService).computeHaulCycles(
            same(truck2),
            argThat(shovels -> shovels.size() == 1 && shovels.get(0).id == shovelId));
        verifyNoMoreInteractions(haulCycleService);
    }

    @Test
    void should_compute_haul_cycles_for_all_trucks_and_not_rethrow() {
        // given
        var truck1Id = UUID.randomUUID();
        var truck2Id = UUID.randomUUID();
        var truck1 = new Truck(truck1Id, "Truck No.1", null, null, null);
        var truck2 = new Truck(truck2Id, "Truck No.2", null, null, null);
        when(registryClient.getEquipment()).thenReturn(Mono.just(new EquipmentResponse(List.of(truck1, truck2))));
        when(haulCycleService.computeHaulCycles(truck1, List.of())).thenReturn(Mono.error(new RuntimeException()));
        when(haulCycleService.computeHaulCycles(truck2, List.of())).thenReturn(Mono.empty());

        // when
        sut.computeHaulCycles();

        // then
        verify(haulCycleService).computeHaulCycles(truck1, List.of());
        verify(haulCycleService).computeHaulCycles(truck2, List.of());
        verifyNoMoreInteractions(haulCycleService);
    }
}
