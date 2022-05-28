package com.github.vkremianskii.pits.processes.model;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class MutableHaulCycle {

    public Long id;
    public UUID shovelId;
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

    public MutableHaulCycle(@Nullable Long id,
                            @Nullable UUID shovelId,
                            @Nullable Instant waitLoadTimestamp,
                            @Nullable Instant startLoadTimestamp,
                            @Nullable Double startLoadLatitude,
                            @Nullable Double startLoadLongitude,
                            @Nullable Instant endLoadTimestamp,
                            @Nullable Integer endLoadPayload,
                            @Nullable Instant startUnloadTimestamp,
                            @Nullable Instant endUnloadTimestamp) {
        this.id = id;
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

    private MutableHaulCycle(HaulCycle haulCycle) {
        id = haulCycle.id();
        shovelId = haulCycle.shovelId();
        waitLoadTimestamp = haulCycle.waitLoadTimestamp();
        startLoadTimestamp = haulCycle.startLoadTimestamp();
        startLoadLatitude = haulCycle.startLoadLatitude();
        startLoadLongitude = haulCycle.startLoadLongitude();
        endLoadTimestamp = haulCycle.endLoadTimestamp();
        endLoadPayload = haulCycle.endLoadPayload();
        startUnloadTimestamp = haulCycle.startUnloadTimestamp();
        endUnloadTimestamp = haulCycle.endUnloadTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableHaulCycle that = (MutableHaulCycle) o;
        return Objects.equals(id, that.id) && Objects.equals(shovelId, that.shovelId) && Objects.equals(waitLoadTimestamp, that.waitLoadTimestamp) && Objects.equals(startLoadTimestamp, that.startLoadTimestamp) && Objects.equals(startLoadLatitude, that.startLoadLatitude) && Objects.equals(startLoadLongitude, that.startLoadLongitude) && Objects.equals(endLoadTimestamp, that.endLoadTimestamp) && Objects.equals(endLoadPayload, that.endLoadPayload) && Objects.equals(startUnloadTimestamp, that.startUnloadTimestamp) && Objects.equals(endUnloadTimestamp, that.endUnloadTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shovelId, waitLoadTimestamp, startLoadTimestamp, startLoadLatitude, startLoadLongitude, endLoadTimestamp, endLoadPayload, startUnloadTimestamp, endUnloadTimestamp);
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

    public static MutableHaulCycle mutableHaulCycle(HaulCycle haulCycle) {
        return new MutableHaulCycle(haulCycle);
    }
}
