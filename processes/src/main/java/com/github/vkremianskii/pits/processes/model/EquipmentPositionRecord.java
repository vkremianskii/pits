package com.github.vkremianskii.pits.processes.model;

import java.time.Instant;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPositionRecord that = (EquipmentPositionRecord) o;
        return id == that.id && equipmentId == that.equipmentId && Double.compare(that.latitude, latitude) == 0 && Double.compare(that.longitude, longitude) == 0 && elevation == that.elevation && Objects.equals(insertTimestamp, that.insertTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, equipmentId, latitude, longitude, elevation, insertTimestamp);
    }
}
