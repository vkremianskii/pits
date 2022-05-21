package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;

import static java.util.Objects.requireNonNull;

public class EquipmentPositionRecord {
    private final long id;
    private final int equipmentId;
    private final double latitude;
    private final double longitude;
    private final int elevation;
    private final Instant insertTimestamp;

    public EquipmentPositionRecord(long id,
                                   int equipmentId,
                                   double latitude,
                                   double longitude,
                                   int elevation,
                                   Instant insertTimestamp) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.insertTimestamp = requireNonNull(insertTimestamp);
    }

    public long id() {
        return id;
    }

    public int equipmentId() {
        return equipmentId;
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public int elevation() {
        return elevation;
    }

    public Instant insertTimestamp() {
        return insertTimestamp;
    }
}
