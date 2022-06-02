package com.github.vkremianskii.pits.processes.logic.fsm;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.proj.coords.UTMPoint;
import com.github.vkremianskii.pits.processes.model.EquipmentPayloadRecord;
import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.processes.model.HaulCycle;
import com.github.vkremianskii.pits.processes.model.MutableHaulCycle;
import com.github.vkremianskii.pits.registry.model.EquipmentState;
import com.github.vkremianskii.pits.registry.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.model.equipment.Truck;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.SortedMap;

import static com.bbn.openmap.proj.Ellipsoid.WGS_84;
import static com.bbn.openmap.proj.coords.UTMPoint.LLtoUTM;
import static com.github.vkremianskii.pits.processes.model.MutableHaulCycle.mutableHaulCycle;
import static java.util.Objects.requireNonNull;

public class HaulCycleFsm {

    private static final Logger LOG = LoggerFactory.getLogger(HaulCycleFsm.class);
    private static final int PAYLOAD_THRESHOLD = 10_000; // kg
    private static final int DEFAULT_SHOVEL_LOAD_RADIUS = 20; // meters

    private final Map<Shovel, ? extends SortedMap<Instant, EquipmentPositionRecord>> shovelToOrderedPositions;
    private final HaulCycleFsmSink haulCycleSink;

    private MutableHaulCycle haulCycle;
    private EquipmentState truckState;
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
            this.truckState = truckStateFromHaulCycle(haulCycle);
        } else {
            this.haulCycle = null;
            this.truckState = null;
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.payload = payload;

        LOG.debug(
            "Initialized: state={} lat={} lng={} payload={} haulCycle={}",
            this.truckState,
            this.latitude,
            this.longitude,
            this.payload,
            this.haulCycle);
    }

    public void consume(EquipmentPositionRecord record) {
        final var timestamp = record.insertTimestamp();
        latitude = record.latitude();
        longitude = record.longitude();
        LOG.debug("Position changed: lat={} lng={}", latitude, longitude);

        if (truckState == null || truckState == Truck.STATE_EMPTY) {
            for (final var shovelEntry : shovelToOrderedPositions.entrySet()) {
                final var shovel = shovelEntry.getKey();
                final var shovelPositions = shovelEntry.getValue();
                final var shovelPositionsBefore = shovelPositions.headMap(timestamp);
                final var shovelPosition = !shovelPositionsBefore.isEmpty() ? shovelPositionsBefore.get(shovelPositionsBefore.lastKey()) : null;
                if (shovelPosition != null) {
                    final var pointLL = new LatLonPoint.Double(latitude, longitude);
                    final var shovelLL = new LatLonPoint.Double(shovelPosition.latitude(), shovelPosition.longitude());
                    final var distance = distance(pointLL, shovelLL);
                    if (distance <= shovel.loadRadius) {
                        truckState = Truck.STATE_WAIT_LOAD;
                        LOG.debug("Truck state changed: " + truckState);
                        if (haulCycle == null || haulCycle.endUnloadTimestamp != null) {
                            haulCycle = new MutableHaulCycle();
                            haulCycleSink.append(haulCycle);
                        }
                        haulCycle.shovelId = shovel.id;
                        haulCycle.waitLoadTimestamp = timestamp;
                        LOG.debug("Haul cycle changed: " + haulCycle);
                        break;
                    }
                }
            }
        } else if (truckState == Truck.STATE_LOAD) {
            if (haulCycle != null) {
                final var startLoadLL = new LatLonPoint.Double(haulCycle.startLoadLatitude, haulCycle.startLoadLongitude);
                final var pointLL = new LatLonPoint.Double(latitude, longitude);
                if (distance(startLoadLL, pointLL) > DEFAULT_SHOVEL_LOAD_RADIUS) {
                    truckState = Truck.STATE_HAUL;
                    LOG.debug("Truck state changed: " + truckState);
                    haulCycle.endLoadTimestamp = timestamp;
                    haulCycle.endLoadPayload = payload;
                    LOG.debug("Haul cycle changed: " + haulCycle);
                }
            }
        }
    }

    public void consume(EquipmentPayloadRecord record) {
        final var timestamp = record.insertTimestamp();
        payload = record.payload();
        LOG.debug("Payload changed: " + payload);

        if (truckState == null || truckState == Truck.STATE_EMPTY || truckState == Truck.STATE_WAIT_LOAD) {
            if (payload > PAYLOAD_THRESHOLD) {
                truckState = Truck.STATE_LOAD;
                LOG.debug("Truck state changed: " + truckState);
                if (haulCycle == null || haulCycle.endUnloadTimestamp != null) {
                    haulCycle = new MutableHaulCycle();
                    haulCycleSink.append(haulCycle);
                }
                haulCycle.startLoadTimestamp = timestamp;
                haulCycle.startLoadLatitude = latitude;
                haulCycle.startLoadLongitude = longitude;
                LOG.debug("Haul cycle changed: " + haulCycle);
            }
        } else if (truckState == Truck.STATE_HAUL) {
            if (haulCycle != null && haulCycle.endLoadPayload - payload > PAYLOAD_THRESHOLD) {
                truckState = Truck.STATE_UNLOAD;
                LOG.debug("Truck state changed: " + truckState);
                haulCycle.startUnloadTimestamp = timestamp;
                LOG.debug("Haul cycle changed: " + haulCycle);
            }
        } else if (truckState == Truck.STATE_UNLOAD) {
            if (haulCycle != null && payload < PAYLOAD_THRESHOLD) {
                truckState = Truck.STATE_EMPTY;
                LOG.debug("Truck state changed: " + truckState);
                haulCycle.endUnloadTimestamp = timestamp;
                LOG.debug("Haul cycle changed: " + haulCycle);
                haulCycle = null;
            }
        }
    }

    public EquipmentState getTruckState() {
        return truckState;
    }

    @Nullable
    private static EquipmentState truckStateFromHaulCycle(HaulCycle haulCycle) {
        if (haulCycle.endUnloadTimestamp() != null) {
            return Truck.STATE_EMPTY;
        }
        if (haulCycle.startUnloadTimestamp() != null) {
            return Truck.STATE_UNLOAD;
        }
        if (haulCycle.endLoadTimestamp() != null) {
            return Truck.STATE_HAUL;
        }
        if (haulCycle.startLoadTimestamp() != null) {
            return Truck.STATE_LOAD;
        }
        if (haulCycle.waitLoadTimestamp() != null) {
            return Truck.STATE_WAIT_LOAD;
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
