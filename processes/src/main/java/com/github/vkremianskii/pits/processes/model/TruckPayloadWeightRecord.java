package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public class TruckPayloadWeightRecord {
    private final long id;
    private final int equipmentId;
    private final int weight;
    private final Instant insertTimestamp;

    public TruckPayloadWeightRecord(long id,
                                    int equipmentId,
                                    int weight,
                                    Instant insertTimestamp) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.weight = weight;
        this.insertTimestamp = requireNonNull(insertTimestamp);
    }

    public long id() {
        return id;
    }

    public int equipmentId() {
        return equipmentId;
    }

    public double weight() {
        return weight;
    }

    public Instant insertTimestamp() {
        return insertTimestamp;
    }
}
