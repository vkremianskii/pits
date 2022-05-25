package com.github.vkremianskii.pits.processes.logic;

import com.github.vkremianskii.pits.processes.model.HaulCycle;

import java.time.Instant;

class MutableHaulCycle {
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
