package com.github.vkremianskii.pits.processes.logic;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.proj.coords.UTMPoint;
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
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

import static com.bbn.openmap.proj.Ellipsoid.WGS_84;
import static com.bbn.openmap.proj.coords.UTMPoint.LLtoUTM;
import static java.util.Objects.requireNonNull;

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
        // TODO: take shovel positions into account
        LOG.info("Computing truck '{}' haul cycles", truck.getId());
        return haulCycleRepository.getLastHaulCycleForTruck(truck.getId())
                .flatMap(c -> computeHaulCycles(truck, c.orElse(null)))
                .then();
    }

    private Mono<Void> computeHaulCycles(Truck truck, @Nullable HaulCycle lastHaulCycle) {
        final var startTimestamp = Optional.ofNullable(lastHaulCycle)
                .map(HaulCycle::getInsertTimestamp)
                .orElse(Instant.EPOCH);

        final var positions = positionRepository.getRecordsForEquipmentAfter(truck.getId(), startTimestamp);
        final var payloads = payloadRepository.getRecordsForEquipmentAfter(truck.getId(), startTimestamp);
        final var lastPosition = positionRepository.getLastRecordForEquipmentBefore(truck.getId(), startTimestamp);
        final var lastPayload = payloadRepository.getLastRecordForEquipmentBefore(truck.getId(), startTimestamp);

        final var startState = Optional.ofNullable(lastHaulCycle)
                .map(HaulCycleService::truckStateFromHaulCycle)
                .orElse(null);

        return Mono.zip(positions, payloads, lastPosition, lastPayload)
                .map(__ -> computeHaulCycles(
                        truck,
                        mergeRecords(__.getT1(), __.getT2()),
                        startState,
                        __.getT3().orElse(null),
                        __.getT4().orElse(null),
                        lastHaulCycle))
                .then();
    }

    private Mono<Void> computeHaulCycles(Truck truck,
                                         TreeMap<Instant, Pair<EquipmentPositionRecord, EquipmentPayloadRecord>> records,
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

        for (final var pair : records.values()) {
            if (pair.left != null) {
                latitude = pair.left.latitude();
                longitude = pair.left.longitude();
                if (state == TruckState.LOAD) {
                    if (haulCycle != null) {
                        final var startLoadLL = new LatLonPoint.Double(haulCycle.startLoadLatitude, haulCycle.startLoadLongitude);
                        final var startLoadUTM = LLtoUTM(startLoadLL, WGS_84, new UTMPoint());
                        final var pointLL = new LatLonPoint.Double(latitude, longitude);
                        final var pointUTM = LLtoUTM(pointLL, WGS_84, new UTMPoint());
                        if (distance(startLoadUTM, pointUTM) > DEFAULT_SHOVEL_LOAD_RADIUS) {
                            state = TruckState.HAUL;
                            haulCycle.endLoadTimestamp = pair.left.insertTimestamp();
                            haulCycle.endLoadPayload = payload;
                        }
                    }
                }
            }
            if (pair.right != null) {
                payload = pair.right.payload();
                if (state == null || state == TruckState.EMPTY || state == TruckState.WAIT_LOAD) {
                    if (payload > PAYLOAD_THRESHOLD) {
                        state = TruckState.LOAD;
                        if (haulCycle == null) {
                            haulCycle = new MutableHaulCycle();
                            haulCycles.add(haulCycle);
                        }
                        haulCycle.startLoadTimestamp = pair.right.insertTimestamp();
                        haulCycle.startLoadLatitude = latitude;
                        haulCycle.startLoadLongitude = longitude;
                    }
                } else if (state == TruckState.HAUL) {
                    if (haulCycle != null && haulCycle.endLoadPayload - payload > PAYLOAD_THRESHOLD) {
                        state = TruckState.UNLOAD;
                        haulCycle.startUnloadTimestamp = pair.right.insertTimestamp();
                    }
                } else if (state == TruckState.UNLOAD) {
                    if (haulCycle != null && payload < PAYLOAD_THRESHOLD) {
                        state = TruckState.EMPTY;
                        haulCycle.endUnloadTimestamp = pair.right.insertTimestamp();
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
                    registryClient.updateEquipmentState(truck.getId(), s).block();
                    return Mono.<Void>empty();
                })
                .orElse(Mono.empty());
    }

    private static TreeMap<Instant, Pair<EquipmentPositionRecord, EquipmentPayloadRecord>> mergeRecords(List<EquipmentPositionRecord> positions,
                                                                                                        List<EquipmentPayloadRecord> payloads) {
        final var records = new TreeMap<Instant, Pair<EquipmentPositionRecord, EquipmentPayloadRecord>>();
        for (final var record : positions) {
            records.put(record.insertTimestamp(), new Pair<>(record, null));
        }
        for (final var record : payloads) {
            records.compute(record.insertTimestamp(), (key, existing) -> {
                if (existing == null) {
                    return new Pair<>(null, record);
                }
                return new Pair<>(existing.left, record);
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

    private static class Pair<L, R> {
        public L left;
        public R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }
    }
}
