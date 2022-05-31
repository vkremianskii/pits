package com.github.vkremianskii.pits.processes.logic;

import com.github.vkremianskii.pits.core.Pair;
import com.github.vkremianskii.pits.core.model.equipment.Shovel;
import com.github.vkremianskii.pits.core.model.equipment.Truck;
import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.HaulCycleRepository;
import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsmFactory;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.processes.model.MutableHaulCycle;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.github.vkremianskii.pits.core.Pair.pair;
import static com.github.vkremianskii.pits.core.PairUtils.pairsToMap;
import static java.util.Objects.requireNonNull;

@Service
public class HaulCycleService {

    private static final Logger LOG = LoggerFactory.getLogger(HaulCycleService.class);

    private final HaulCycleRepository haulCycleRepository;
    private final EquipmentPositionRepository positionRepository;
    private final EquipmentPayloadRepository payloadRepository;
    private final RegistryClient registryClient;
    private final HaulCycleFsmFactory haulCycleFsmFactory;

    public HaulCycleService(HaulCycleRepository haulCycleRepository,
                            EquipmentPositionRepository positionRepository,
                            EquipmentPayloadRepository payloadRepository,
                            RegistryClient registryClient,
                            HaulCycleFsmFactory haulCycleFsmFactory) {
        this.haulCycleRepository = requireNonNull(haulCycleRepository);
        this.positionRepository = requireNonNull(positionRepository);
        this.payloadRepository = requireNonNull(payloadRepository);
        this.registryClient = requireNonNull(registryClient);
        this.haulCycleFsmFactory = requireNonNull(haulCycleFsmFactory);
    }

    @Transactional
    public Mono<Void> computeHaulCycles(Truck truck, List<Shovel> shovels) {
        return haulCycleRepository.getLastHaulCycleForTruck(truck.id)
            .flatMap(c -> computeHaulCycles(truck, shovels, c.orElse(null)));
    }

    private Mono<Void> computeHaulCycles(Truck truck,
                                         List<Shovel> shovels,
                                         @Nullable HaulCycle lastHaulCycle) {
        final var startTimestamp = Optional.ofNullable(lastHaulCycle)
            .map(HaulCycle::insertTimestamp)
            .orElse(Instant.EPOCH);

        final var positions = positionRepository.getRecordsForEquipmentAfter(truck.id, startTimestamp);
        final var payloads = payloadRepository.getRecordsForEquipmentAfter(truck.id, startTimestamp);
        final var lastPosition = positionRepository.getLastRecordForEquipmentBefore(truck.id, startTimestamp);
        final var lastPayload = payloadRepository.getLastRecordForEquipmentBefore(truck.id, startTimestamp);

        final var shovelToPositions = Flux.concat(shovels.stream()
                .map(shovel -> positionRepository.getRecordsForEquipmentAfter(shovel.id, startTimestamp)
                    .map(records -> pair(shovel, records)))
                .toList())
            .collectList();

        final var shovelToLastPosition = Flux.concat(shovels.stream()
                .map(shovel -> positionRepository.getLastRecordForEquipmentBefore(shovel.id, startTimestamp)
                    .map(record -> pair(shovel, record)))
                .toList())
            .collectList();

        return Mono.zip(positions, payloads, lastPosition, lastPayload, shovelToPositions, shovelToLastPosition)
            .flatMap(__ -> computeHaulCycles(
                truck,
                mergeRecords(__.getT1(), __.getT2()),
                shovelToOrderedPositions(pairsToMap(__.getT5()), pairsToMap(__.getT6())),
                __.getT3().orElse(null),
                __.getT4().orElse(null),
                lastHaulCycle));
    }

    private Map<Shovel, SortedMap<Instant, EquipmentPositionRecord>> shovelToOrderedPositions(Map<Shovel, List<EquipmentPositionRecord>> shovelToPosition,
                                                                                              Map<Shovel, Optional<EquipmentPositionRecord>> shovelToLastPosition) {
        final var result = new HashMap<Shovel, SortedMap<Instant, EquipmentPositionRecord>>();
        for (final var shovel : shovelToPosition.keySet()) {
            if (!result.containsKey(shovel)) {
                result.put(shovel, new TreeMap<>());
            }
        }
        for (final var shovel : shovelToLastPosition.keySet()) {
            if (!result.containsKey(shovel)) {
                result.put(shovel, new TreeMap<>());
            }
        }

        final var shovels = result.keySet();
        for (final var shovel : shovels) {
            final var orderedPositions = result.get(shovel);

            final var lastPosition = shovelToLastPosition.get(shovel);
            lastPosition.ifPresent(p -> orderedPositions.put(p.insertTimestamp(), p));

            final var positions = shovelToPosition.get(shovel);
            for (final var position : positions) {
                orderedPositions.put(position.insertTimestamp(), position);
            }
        }

        return result;
    }

    private Mono<Void> computeHaulCycles(Truck truck,
                                         SortedMap<Instant, Pair<EquipmentPositionRecord, EquipmentPayloadRecord>> orderedRecords,
                                         Map<Shovel, SortedMap<Instant, EquipmentPositionRecord>> shovelToOrderedPositions,
                                         @Nullable EquipmentPositionRecord startPosition,
                                         @Nullable EquipmentPayloadRecord startPayload,
                                         @Nullable HaulCycle lastHaulCycle) {
        final var haulCycles = new ArrayList<MutableHaulCycle>();
        final var haulCycleFsm = haulCycleFsmFactory.create(shovelToOrderedPositions, haulCycles::add);
        haulCycleFsm.initialize(
            lastHaulCycle,
            startPosition != null ? startPosition.latitude() : null,
            startPosition != null ? startPosition.longitude() : null,
            startPayload != null ? startPayload.payload() : null);

        for (final var entry : orderedRecords.entrySet()) {
            final var records = entry.getValue();
            final var positionRecord = records.left();
            final var payloadRecord = records.right();
            if (positionRecord != null) {
                haulCycleFsm.consume(positionRecord);
            }
            if (payloadRecord != null) {
                haulCycleFsm.consume(payloadRecord);
            }
        }

        return Flux.fromIterable(haulCycles)
            .flatMap(hc -> {
                if (hc.id != null) {
                    LOG.info("Updating haul cycle " + hc);
                    return haulCycleRepository.update(
                        hc.id,
                        hc.shovelId,
                        hc.waitLoadTimestamp,
                        hc.startLoadTimestamp,
                        hc.startLoadLatitude,
                        hc.startLoadLongitude,
                        hc.endLoadTimestamp,
                        hc.endLoadPayload,
                        hc.startUnloadTimestamp,
                        hc.endUnloadTimestamp);
                } else {
                    LOG.info("Inserting haul cycle: " + hc);
                    return haulCycleRepository.insert(
                        truck.id,
                        hc.shovelId,
                        hc.waitLoadTimestamp,
                        hc.startLoadTimestamp,
                        hc.startLoadLatitude,
                        hc.startLoadLongitude,
                        hc.endLoadTimestamp,
                        hc.endLoadPayload,
                        hc.startUnloadTimestamp,
                        hc.endUnloadTimestamp);
                }
            })
            .then(Mono.defer(() -> Mono.justOrEmpty(haulCycleFsm.getTruckState())))
            .flatMap(state -> {
                LOG.info("Updating truck '{}' state in registry: {}", truck.id, state);
                return registryClient.updateEquipmentState(truck.id, state);
            })
            .then();
    }

    private static SortedMap<Instant, Pair<EquipmentPositionRecord, EquipmentPayloadRecord>> mergeRecords(List<EquipmentPositionRecord> positions,
                                                                                                          List<EquipmentPayloadRecord> payloads) {
        final var records = new TreeMap<Instant, Pair<EquipmentPositionRecord, EquipmentPayloadRecord>>();
        for (final var record : positions) {
            records.put(record.insertTimestamp(), pair(record, null));
        }
        for (final var record : payloads) {
            records.compute(record.insertTimestamp(), (key, existing) -> {
                if (existing == null) {
                    return pair(null, record);
                }
                return pair(existing.left(), record);
            });
        }
        return records;
    }
}
