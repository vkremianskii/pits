package com.github.vkremianskii.pits.processes.job;

import com.github.vkremianskii.pits.processes.logic.HaulCycleService;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.model.equipment.Truck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.vkremianskii.pits.registry.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.model.EquipmentType.TRUCK;
import static java.util.Objects.requireNonNull;

@Component
public class HaulCycleJob {

    private static final Logger LOG = LoggerFactory.getLogger(HaulCycleJob.class);

    private final RegistryClient registryClient;
    private final HaulCycleService haulCycleService;
    private final ReactiveTransactionManager transactionManager;

    public HaulCycleJob(RegistryClient registryClient,
                        HaulCycleService haulCycleService,
                        ReactiveTransactionManager transactionManager) {
        this.registryClient = requireNonNull(registryClient);
        this.haulCycleService = requireNonNull(haulCycleService);
        this.transactionManager = requireNonNull(transactionManager);
    }

    @Scheduled(cron = "${jobs.haul-cycle.cron}")
    public void computeHaulCycles() {
        LOG.info("Haul cycle computation started");
        registryClient.getEquipment()
            .flatMap(response -> {
                final var trucks = response.equipment().stream()
                    .filter(e -> e.type == TRUCK)
                    .map(e -> (Truck) e)
                    .toList();
                final var shovels = response.equipment().stream()
                    .filter(e -> e.type == SHOVEL)
                    .map(e -> (Shovel) e)
                    .toList();
                return Flux.fromIterable(trucks)
                    .flatMap(truck -> {
                        LOG.info("Computing truck '{}' haul cycles", truck.id);
                        return haulCycleService.computeHaulCycles(truck, shovels)
                            .onErrorResume(e -> {
                                LOG.error("Error while computing haul cycles for truck: " + truck.id, e);
                                return Mono.empty();
                            });
                    })
                    .then();
            })
            .doOnSuccess(__ -> LOG.info("Haul cycle computation finished"))
            .doOnError(e -> LOG.error("Haul cycle computation failed", e))
            .block();
    }
}
