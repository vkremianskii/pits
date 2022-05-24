package com.github.vkremianskii.pits.processes.logic;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.proj.coords.UTMPoint;
import com.github.vkremianskii.pits.core.types.Pair;
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
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

import static com.bbn.openmap.proj.Ellipsoid.WGS_84;
import static com.bbn.openmap.proj.coords.UTMPoint.LLtoUTM;
import static com.github.vkremianskii.pits.core.types.Pair.pair;
import static com.github.vkremianskii.pits.core.types.PairUtils.pairsToMap;
import static java.util.Objects.requireNonNull;
import static reactor.core.scheduler.Schedulers.parallel;

@Service
public class HaulCycleService {
    private static final Logger LOG = LoggerFactory.getLogger(HaulCycleService.class);
    private static final int PAYLOAD_THRESHOLD = 10_000; // kg
    private static final int DEFAULT_SHOVEL_LOAD_RADIUS = 20; // meters

    private final HaulCycleRepository haulCycleRepository;
    private final EquipmentPositionRepository positionRepository;
    private final EquipmentPayloadRepository payloadRepository;
    private final RegistryClient registryClient;

    public HaulCycleService(HaulCycleRepository haulCycleRepository,
                            EquipmentPositionRepository positionRepository,
                            EquipmentPayloadRepository payloadRepository,
                            RegistryClient registryClient) {
        this.haulCycleRepository = requireNonNull(haulCycleRepository);
        this.positionRepository = requireNonNull(positionRepository);
        this.payloadRepository = requireNonNull(payloadRepository);
        this.registryClient = requireNonNull(registryClient);
    }

    public Mono<Void> computeHaulCycles(Truck truck, List<Shovel> shovels) {
        LOG.info("Computing truck '{}' haul cycles", truck.getId());
        return haulCycleRepository.getLastHaulCycleForTruck(truck.getId())
                .flatMap(c -> computeHaulCycles(truck, shovels, c.orElse(null)))
                .subscribeOn(parallel());
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

        final var startState = Optional.ofNullable(lastHaulCycle)
                .map(HaulCycleService::truckStateFromHaulCycle)
                .orElse(null);

        return Mono.zip(positions, payloads, lastPosition, lastPayload, shovelToPositions, shovelToLastPosition)
                .flatMap(__ -> computeHaulCycles(
                        truck,
                        mergeRecords(__.getT1(), __.getT2()),
                        shovelToOrderedPositions(pairsToMap(__.getT5()), pairsToMap(__.getT6())),
                        startState,
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
                                         @Nullable TruckState startState,
                                         @Nullable EquipmentPositionRecord startPosition,
                                         @Nullable EquipmentPayloadRecord startPayload,
                                         @Nullable HaulCycle lastHaulCycle) {
        TruckState state = startState;
        Double latitude = startPosition != null ? startPosition.latitude() : null;
        Double longitude = startPosition != null ? startPosition.longitude() : null;
        Integer payload = startPayload != null ? startPayload.payload() : null;

        List<MutableHaulCycle> haulCycles = new ArrayList<>();
        MutableHaulCycle haulCycle = Optional.ofNullable(lastHaulCycle).map(MutableHaulCycle::new).orElse(null);
        if (haulCycle != null) {
            haulCycles.add(haulCycle);
        }

        for (final var entry : orderedRecords.entrySet()) {
            final var timestamp = entry.getKey();
            final var records = entry.getValue();
            final var positionRecord = records.getLeft();
            final var payloadRecord = records.getRight();
            if (positionRecord != null) {
                latitude = positionRecord.latitude();
                longitude = positionRecord.longitude();
                if (state == null || state == TruckState.EMPTY) {
                    for (final var shovelEntry : shovelToOrderedPositions.entrySet()) {
                        final var shovel = shovelEntry.getKey();
                        final var shovelPositions = shovelEntry.getValue();
                        final var shovelPositionsBefore = shovelPositions.headMap(timestamp);
                        final var shovelPosition = !shovelPositionsBefore.isEmpty() ? shovelPositionsBefore.get(shovelPositionsBefore.lastKey()) : null;
                        if (shovelPosition != null) {
                            final var pointLL = new LatLonPoint.Double(latitude, longitude);
                            final var shovelLL = new LatLonPoint.Double(shovelPosition.latitude(), shovelPosition.longitude());
                            final var distance = distance(pointLL, shovelLL);
                            if (distance <= shovel.getLoadRadius()) {
                                state = TruckState.WAIT_LOAD;
                                if (haulCycle == null) {
                                    haulCycle = new MutableHaulCycle();
                                    haulCycles.add(haulCycle);
                                }
                                haulCycle.shovelId = shovel.getId();
                                haulCycle.waitLoadTimestamp = timestamp;
                            }
                        }
                    }
                } else if (state == TruckState.LOAD) {
                    if (haulCycle != null) {
                        final var startLoadLL = new LatLonPoint.Double(haulCycle.startLoadLatitude, haulCycle.startLoadLongitude);
                        final var pointLL = new LatLonPoint.Double(latitude, longitude);
                        if (distance(startLoadLL, pointLL) > DEFAULT_SHOVEL_LOAD_RADIUS) {
                            state = TruckState.HAUL;
                            haulCycle.endLoadTimestamp = timestamp;
                            haulCycle.endLoadPayload = payload;
                        }
                    }
                }
            }
            if (payloadRecord != null) {
                payload = payloadRecord.payload();
                if (state == null || state == TruckState.EMPTY || state == TruckState.WAIT_LOAD) {
                    if (payload > PAYLOAD_THRESHOLD) {
                        state = TruckState.LOAD;
                        if (haulCycle == null) {
                            haulCycle = new MutableHaulCycle();
                            haulCycles.add(haulCycle);
                        }
                        haulCycle.startLoadTimestamp = timestamp;
                        haulCycle.startLoadLatitude = latitude;
                        haulCycle.startLoadLongitude = longitude;
                    }
                } else if (state == TruckState.HAUL) {
                    if (haulCycle != null && haulCycle.endLoadPayload - payload > PAYLOAD_THRESHOLD) {
                        state = TruckState.UNLOAD;
                        haulCycle.startUnloadTimestamp = timestamp;
                    }
                } else if (state == TruckState.UNLOAD) {
                    if (haulCycle != null && payload < PAYLOAD_THRESHOLD) {
                        state = TruckState.EMPTY;
                        haulCycle.endUnloadTimestamp = timestamp;
                        haulCycle = null;
                    }
                }
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

        return Optional.ofNullable(state)
                .map(s -> {
                    LOG.info("Updating truck '{}' state in registry: {}", truck.getId(), s);
                    return registryClient.updateEquipmentState(truck.getId(), s);
                })
                .orElse(Mono.empty());
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

    @Nullable
    private static TruckState truckStateFromHaulCycle(HaulCycle haulCycle) {
        if (haulCycle.getEndUnloadTimestamp() != null) {
            return TruckState.EMPTY;
        }
        if (haulCycle.getStartUnloadTimestamp() != null) {
            return TruckState.UNLOAD;
        }
        if (haulCycle.getEndLoadTimestamp() != null) {
            return TruckState.HAUL;
        }
        if (haulCycle.getStartLoadTimestamp() != null) {
            return TruckState.LOAD;
        }
        if (haulCycle.getWaitLoadTimestamp() != null) {
            return TruckState.WAIT_LOAD;
        }
        return null;
    }

    private static double distance(LatLonPoint left, LatLonPoint right) {
        final var leftUTM = LLtoUTM(left, WGS_84, new UTMPoint());
        final var rightUTM = LLtoUTM(right, WGS_84, new UTMPoint());
        return distance(leftUTM, rightUTM);
    }

    private static double distance(UTMPoint left, UTMPoint right) {
        double dx = left.easting - right.easting;
        double dy = left.northing - right.northing;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static class MutableHaulCycle {
        public Long id;
        public Integer shovelId;
        public Instant waitLoadTimestamp;
        public Instant startLoadTimestamp;
        public Double startLoadLatitude;
        public Double startLoadLongitude;
        public Instant endLoadTimestamp;
        public Integer endLoadPayload;
        public Instant startUnloadTimestamp;
        public Instant endUnloadTimestamp;

        public MutableHaulCycle() {
        }

        public MutableHaulCycle(HaulCycle haulCycle) {
            id = haulCycle.getId();
            shovelId = haulCycle.getShovelId();
            waitLoadTimestamp = haulCycle.getWaitLoadTimestamp();
            startLoadTimestamp = haulCycle.getStartLoadTimestamp();
            startLoadLatitude = haulCycle.getStartLoadLatitude();
            startLoadLongitude = haulCycle.getStartLoadLongitude();
            endLoadTimestamp = haulCycle.getEndLoadTimestamp();
            endLoadPayload = haulCycle.getEndLoadPayload();
            startUnloadTimestamp = haulCycle.getStartUnloadTimestamp();
            endUnloadTimestamp = haulCycle.getEndUnloadTimestamp();
        }

        @Override
        public String toString() {
            return "MutableHaulCycle{" +
                    "id=" + id +
                    ", shovelId=" + shovelId +
                    ", waitLoadTimestamp=" + waitLoadTimestamp +
                    ", startLoadTimestamp=" + startLoadTimestamp +
                    ", startLoadLatitude=" + startLoadLatitude +
                    ", startLoadLongitude=" + startLoadLongitude +
                    ", endLoadTimestamp=" + endLoadTimestamp +
                    ", endLoadPayload=" + endLoadPayload +
                    ", startUnloadTimestamp=" + startUnloadTimestamp +
                    ", endUnloadTimestamp=" + endUnloadTimestamp +
                    '}';
        }
    }
}
