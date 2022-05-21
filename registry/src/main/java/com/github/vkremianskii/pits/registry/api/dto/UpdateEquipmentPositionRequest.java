package com.github.vkremianskii.pits.registry.api.dto;

public class UpdateEquipmentPositionRequest {
    private final int equipmentId;
    private final double latitude;
    private final double longitude;
    private final int elevation;

    public UpdateEquipmentPositionRequest(int equipmentId, double latitude, double longitude, int elevation) {
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
}
