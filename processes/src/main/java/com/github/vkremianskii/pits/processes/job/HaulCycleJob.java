package com.github.vkremianskii.pits.processes.job;

import com.github.vkremianskii.pits.processes.logic.HaulCycleService;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static java.util.Objects.requireNonNull;

@Component
public class HaulCycleJob {
    private static final Logger LOG = LoggerFactory.getLogger(HaulCycleJob.class);

    private final RegistryClient registryClient;
    private final HaulCycleService haulCycleService;

    public HaulCycleJob(RegistryClient registryClient,
                        HaulCycleService haulCycleService) {
        this.registryClient = requireNonNull(registryClient);
        this.haulCycleService = requireNonNull(haulCycleService);
    }

    @Scheduled(cron = "0 * * * * *")
    public void computeHaulCycles() {
        try {
            LOG.info("Haul cycle computation started");
            final var equipment = registryClient.getEquipment().block();
            final var trucks = equipment.stream()
                    .filter(e -> e.getType() == TRUCK)
                    .map(e -> (Truck) e)
                    .toList();
            final var shovels = equipment.stream()
                    .filter(e -> e.getType() == SHOVEL)
                    .map(e -> (Shovel) e)
                    .toList();
            trucks.forEach(truck -> haulCycleService.computeHaulCycles(truck, shovels).block());
            LOG.info("Haul cycle computation finished");
        } catch (Exception e) {
            LOG.error("Haul cycle computation failed", e);
        }
    }
}
