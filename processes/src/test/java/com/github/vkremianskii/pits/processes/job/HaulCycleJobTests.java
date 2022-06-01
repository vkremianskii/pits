package com.github.vkremianskii.pits.processes.job;

import com.github.vkremianskii.pits.processes.logic.HaulCycleService;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.dto.EquipmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.vkremianskii.pits.registry.TestEquipment.aDozer;
import static com.github.vkremianskii.pits.registry.TestEquipment.aShovel;
import static com.github.vkremianskii.pits.registry.TestEquipment.aTruck;
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
    ReactiveTransactionManager transactionManager = mock(ReactiveTransactionManager.class);
    HaulCycleJob sut = new HaulCycleJob(registryClient, haulCycleService, transactionManager);

    @BeforeEach
    void setup() {
        when(transactionManager.getReactiveTransaction(any()))
            .thenReturn(Mono.just(mock(ReactiveTransaction.class)));
        when(transactionManager.commit(any()))
            .thenReturn(Mono.empty());
        when(transactionManager.rollback(any()))
            .thenReturn(Mono.empty());
    }

    @Test
    void should_compute_haul_cycles_for_all_trucks() {
        // given
        var dozer = aDozer();
        var shovel = aShovel();
        var truck1 = aTruck();
        var truck2 = aTruck();
        when(registryClient.getEquipment()).thenReturn(Mono.just(new EquipmentResponse(List.of(dozer, shovel, truck1, truck2))));
        when(haulCycleService.computeHaulCycles(any(), any())).thenReturn(Mono.empty());

        // when
        sut.computeHaulCycles();

        // then
        verify(haulCycleService).computeHaulCycles(
            same(truck1),
            argThat(shovels -> shovels.size() == 1 && shovels.get(0).id == shovel.id));
        verify(haulCycleService).computeHaulCycles(
            same(truck2),
            argThat(shovels -> shovels.size() == 1 && shovels.get(0).id == shovel.id));
        verifyNoMoreInteractions(haulCycleService);
    }

    @Test
    void should_compute_haul_cycles_for_all_trucks_and_not_rethrow() {
        // given
        var truck1 = aTruck();
        var truck2 = aTruck();
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
