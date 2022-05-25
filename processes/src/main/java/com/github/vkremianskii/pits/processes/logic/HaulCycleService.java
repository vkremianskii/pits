package com.github.vkremianskii.pits.processes.logic;

import com.github.vkremianskii.pits.core.types.Pair;
import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.HaulCycleRepository;
import com.github.vkremianskii.pits.processes.logic.fsm.HaulCycleFsmFactory;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.processes.model.MutableHaulCycle;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

import static com.github.vkremianskii.pits.core.types.Pair.pair;
import static com.github.vkremianskii.pits.core.types.PairUtils.pairsToMap;
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

    public Mono<Void> computeHaulCycles(Truck truck, List<Shovel> shovels) {
        return haulCycleRepository.getLastHaulCycleForTruck(truck.getId())
                .flatMap(c -> computeHaulCycles(truck, shovels, c.orElse(null)));
    }

    private Mono<Void> computeHaulCycles(Truck truck,
                                         List<Shovel> shovels,
                                         @Nullable HaulCycle lastHaulCycle) {
        final var startTimestamp = Optional.ofNullable(lastHaulCycle)
                .map(HaulCycle::getInsertTimestamp)
                .orElse(Instant.EPOCH);

        final var positions = positionRepository.getRecordsForEquipmentAfter(truck.getId(), startTimestamp);
        final var payloads = payloadRepository.getRecordsForEquipmentAfter(truck.getId(), startTimestamp);
        final var lastPosition = positionRepository.getLastRecordForEquipmentBefore(truck.getId(), startTimestamp);
        final var lastPayload = payloadRepository.getLastRecordForEquipmentBefore(truck.getId(), startTimestamp);

        final var shovelToPositions = Flux.concat(shovels.stream()
                        .map(shovel -> positionRepository.getRecordsForEquipmentAfter(shovel.getId(), startTimestamp)
                                .map(records -> pair(shovel, records)))
                        .toList())
                .collectList();

        final var shovelToLastPosition = Flux.concat(shovels.stream()
                        .map(shovel -> positionRepository.getLastRecordForEquipmentBefore(shovel.getId(), startTimestamp)
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
            final var positionRecord = records.getLeft();
            final var payloadRecord = records.getRight();
            if (positionRecord != null) {
                haulCycleFsm.consume(positionRecord);
            }
            if (payloadRecord != null) {
                haulCycleFsm.consume(payloadRecord);
            }
        }

        for (final var hc : haulCycles) {
            if (hc.id != null) {
                LOG.info("Updating haul cycle " + hc);
                haulCycleRepository.update(
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
                haulCycleRepository.insert(
                        truck.getId(),
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
        }

        final var state = haulCycleFsm.getState();
        if (state == null) {
            return Mono.empty();
        }

        LOG.info("Updating truck '{}' state in registry: {}", truck.getId(), state);
        return registryClient.updateEquipmentState(truck.getId(), state);
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
                return pair(existing.getLeft(), record);
            });
        }
        return records;
    }
}
