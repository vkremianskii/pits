package com.github.vkremianskii.pits.processes.job;

import com.github.vkremianskii.pits.processes.logic.HaulCycleService;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Mono;

import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static java.util.Objects.requireNonNull;

@Component
public class HaulCycleJob {
    private static final Logger LOG = LoggerFactory.getLogger(HaulCycleJob.class);

    private final RegistryClient registryClient;
    private final HaulCycleService haulCycleService;
    private final PlatformTransactionManager transactionManager;

    public HaulCycleJob(RegistryClient registryClient,
                        HaulCycleService haulCycleService,
                        PlatformTransactionManager transactionManager) {
        this.registryClient = requireNonNull(registryClient);
        this.haulCycleService = requireNonNull(haulCycleService);
        this.transactionManager = requireNonNull(transactionManager);
    }

    @Scheduled(cron = "${jobs.haul-cycle.cron}")
    public void computeHaulCycles() {
        LOG.info("Haul cycle computation started");
        registryClient.getEquipment()
                .flatMap(equipment -> {
                    final var trucks = equipment.stream()
                            .filter(e -> e.getType() == TRUCK)
                            .map(e -> (Truck) e)
                            .toList();
                    final var shovels = equipment.stream()
                            .filter(e -> e.getType() == SHOVEL)
                            .map(e -> (Shovel) e)
                            .toList();
                    return Mono.when(trucks.stream().map(truck -> {
                        LOG.info("Computing truck '{}' haul cycles", truck.getId());
                        final var tx = transactionManager.getTransaction(null);
                        return haulCycleService.computeHaulCycles(truck, shovels)
                                .doOnSuccess(__ -> transactionManager.commit(tx))
                                .onErrorResume(e -> {
                                    LOG.error("Error while computing truck '" + truck.getId() + "' haul cycles", e);
                                    transactionManager.rollback(tx);
                                    return Mono.empty();
                                });
                    }).toList()).then();
                })
                .doOnSuccess(__ -> LOG.info("Haul cycle computation finished"))
                .doOnError(e -> LOG.error("Haul cycle computation failed", e))
                .block();
    }
}
