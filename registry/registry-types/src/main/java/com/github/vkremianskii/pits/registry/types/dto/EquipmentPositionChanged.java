package com.github.vkremianskii.pits.registry.types.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class EquipmentPositionChanged {
    private final int equipmentId;
    private final double latitude;
    private final double longitude;
    private final int elevation;

    @JsonCreator
    public EquipmentPositionChanged(@JsonProperty("equipmentId") int equipmentId,
                                    @JsonProperty("latitude") double latitude,
                                    @JsonProperty("longitude") double longitude,
                                    @JsonProperty("elevation") int elevation) {
        this.equipmentId = equipmentId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getElevation() {
        return elevation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPositionChanged that = (EquipmentPositionChanged) o;
        return equipmentId == that.equipmentId && Double.compare(that.latitude, latitude) == 0 && Double.compare(that.longitude, longitude) == 0 && elevation == that.elevation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId, latitude, longitude, elevation);
    }
}
