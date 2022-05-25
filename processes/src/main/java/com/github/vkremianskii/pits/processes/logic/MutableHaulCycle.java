package com.github.vkremianskii.pits.processes.logic;

import com.github.vkremianskii.pits.processes.model.HaulCycle;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Objects;

public class MutableHaulCycle {
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

    public MutableHaulCycle(@Nullable Long id,
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
