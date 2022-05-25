package com.github.vkremianskii.pits.processes.logic.fsm;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.proj.coords.UTMPoint;
import com.github.vkremianskii.pits.processes.logic.MutableHaulCycle;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.SortedMap;

import static com.bbn.openmap.proj.Ellipsoid.WGS_84;
import static com.bbn.openmap.proj.coords.UTMPoint.LLtoUTM;
import static com.github.vkremianskii.pits.processes.logic.MutableHaulCycle.mutableHaulCycle;
import static java.util.Objects.requireNonNull;

public class HaulCycleFsm {
    private static final int PAYLOAD_THRESHOLD = 10_000; // kg
    private static final int DEFAULT_SHOVEL_LOAD_RADIUS = 20; // meters

    private final Map<Shovel, ? extends SortedMap<Instant, EquipmentPositionRecord>> shovelToOrderedPositions;
    private final HaulCycleFsmSink haulCycleSink;

    private MutableHaulCycle haulCycle;
    private TruckState state;
    private Double latitude;
    private Double longitude;
    private Integer payload;

    public HaulCycleFsm(Map<Shovel, ? extends SortedMap<Instant, EquipmentPositionRecord>> shovelToOrderedPositions,
                        HaulCycleFsmSink haulCycleSink) {
        this.shovelToOrderedPositions = requireNonNull(shovelToOrderedPositions);
        this.haulCycleSink = requireNonNull(haulCycleSink);
    }

    public void initialize(@Nullable HaulCycle haulCycle,
                           @Nullable Double latitude,
                           @Nullable Double longitude,
                           @Nullable Integer payload) {
        if (haulCycle != null) {
            this.haulCycle = mutableHaulCycle(haulCycle);
            this.haulCycleSink.append(this.haulCycle);
            this.state = truckStateFromHaulCycle(haulCycle);
        } else {
            this.haulCycle = null;
            this.state = null;
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.payload = payload;
    }

    public void consume(EquipmentPositionRecord record) {
        final var timestamp = record.insertTimestamp();
        latitude = record.latitude();
        longitude = record.longitude();
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
                            haulCycleSink.append(haulCycle);
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

    public void consume(EquipmentPayloadRecord record) {
        final var timestamp = record.insertTimestamp();
        payload = record.payload();
        if (state == null || state == TruckState.EMPTY || state == TruckState.WAIT_LOAD) {
            if (payload > PAYLOAD_THRESHOLD) {
                state = TruckState.LOAD;
                if (haulCycle == null) {
                    haulCycle = new MutableHaulCycle();
                    haulCycleSink.append(haulCycle);
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

    public TruckState getState() {
        return state;
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
}
