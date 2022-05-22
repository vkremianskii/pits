package com.github.vkremianskii.pits.processes.model;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class HaulCycle {
    private final long id;
    private final int truckId;
    private final Instant insertTimestamp;
    private final Integer shovelId;
    private final Instant waitLoadTimestamp;
    private final Instant startLoadTimestamp;
    private final Double startLoadLatitude;
    private final Double startLoadLongitude;
    private final Instant endLoadTimestamp;
    private final Integer endLoadPayload;
    private final Instant startUnloadTimestamp;
    private final Instant endUnloadTimestamp;

    public HaulCycle(long id,
                     int truckId,
                     Instant insertTimestamp,
                     @Nullable Integer shovelId,
                     @Nullable Instant waitLoadTimestamp,
                     @Nullable Instant startLoadTimestamp,
                     @Nullable Double startLoadLatitude,
                     @Nullable Double startLoadLongitude,
                     @Nullable Instant endLoadTimestamp,
                     @Nullable Integer endLoadPayload,
                     @Nullable Instant startUnloadTimestamp,
                     @Nullable Instant endUnloadTimestamp) {
        this.id = id;
        this.truckId = truckId;
        this.insertTimestamp = requireNonNull(insertTimestamp);
        this.shovelId = shovelId;
        this.waitLoadTimestamp = waitLoadTimestamp;
        this.startLoadTimestamp = startLoadTimestamp;
        this.startLoadLatitude = startLoadLatitude;
        this.startLoadLongitude = startLoadLongitude;
        this.endLoadTimestamp = endLoadTimestamp;
        this.endLoadPayload = endLoadPayload;
        this.startUnloadTimestamp = startUnloadTimestamp;
        this.endUnloadTimestamp = endUnloadTimestamp;
    }

    public long getId() {
        return id;
    }

    public int getTruckId() {
        return truckId;
    }

    public Instant getInsertTimestamp() {
        return insertTimestamp;
    }

    @Nullable
    public Integer getShovelId() {
        return shovelId;
    }

    @Nullable
    public Instant getWaitLoadTimestamp() {
        return waitLoadTimestamp;
    }

    @Nullable
    public Instant getStartLoadTimestamp() {
        return startLoadTimestamp;
    }

    @Nullable
    public Double getStartLoadLatitude() {
        return startLoadLatitude;
    }

    @Nullable
    public Double getStartLoadLongitude() {
        return startLoadLongitude;
    }

    @Nullable
    public Instant getEndLoadTimestamp() {
        return endLoadTimestamp;
    }

    @Nullable
    public Integer getEndLoadPayload() {
        return endLoadPayload;
    }

    @Nullable
    public Instant getStartUnloadTimestamp() {
        return startUnloadTimestamp;
    }

    @Nullable
    public Instant getEndUnloadTimestamp() {
        return endUnloadTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HaulCycle)) return false;
        HaulCycle haulCycle = (HaulCycle) o;
        return id == haulCycle.id &&
                truckId == haulCycle.truckId &&
                Objects.equals(insertTimestamp, haulCycle.insertTimestamp) &&
                Objects.equals(shovelId, haulCycle.shovelId) &&
                Objects.equals(waitLoadTimestamp, haulCycle.waitLoadTimestamp) &&
                Objects.equals(startLoadTimestamp, haulCycle.startLoadTimestamp) &&
                Objects.equals(startLoadLatitude, haulCycle.startLoadLatitude) &&
                Objects.equals(startLoadLongitude, haulCycle.startLoadLongitude) &&
                Objects.equals(endLoadTimestamp, haulCycle.endLoadTimestamp) &&
                Objects.equals(endLoadPayload, haulCycle.endLoadPayload) &&
                Objects.equals(startUnloadTimestamp, haulCycle.startUnloadTimestamp) &&
                Objects.equals(endUnloadTimestamp, haulCycle.endUnloadTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                truckId,
                insertTimestamp,
                shovelId,
                waitLoadTimestamp,
                startLoadTimestamp,
                startLoadLatitude,
                startLoadLongitude,
                endLoadTimestamp,
                endLoadPayload,
                startUnloadTimestamp,
                endUnloadTimestamp);
    }
}
