package com.github.vkremianskii.pits.processes.job;

import com.github.vkremianskii.pits.processes.logic.HaulCycleService;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;

class HaulCycleJobTests {
    RegistryClient registryClient = mock(RegistryClient.class);
    HaulCycleService haulCycleService = mock(HaulCycleService.class);
    HaulCycleJob sut = new HaulCycleJob(registryClient, haulCycleService);

    @Test
    void should_compute_haul_cycles_for_all_trucks() {
        // given
        var dozer = new Dozer(1, "Dozer No.1", null, null);
        var shovel = new Shovel(2, "Shovel No.1", 20, null, null);
        var truck1 = new Truck(3, "Truck No.1", null, null, null);
        var truck2 = new Truck(4, "Truck No.2", null, null, null);
        when(registryClient.getEquipment()).thenReturn(Mono.just(List.of(dozer, shovel, truck1, truck2)));
        when(haulCycleService.computeHaulCycles(any(), any())).thenReturn(Mono.empty());

        // when
        sut.computeHaulCycles();

        // then
        verify(haulCycleService).computeHaulCycles(
                same(truck1),
                argThat(shovels -> shovels.size() == 1 && shovels.get(0).getId() == 2));
        verify(haulCycleService).computeHaulCycles(
                same(truck2),
                argThat(shovels -> shovels.size() == 1 && shovels.get(0).getId() == 2));
        verifyNoMoreInteractions(haulCycleService);
    }
}
