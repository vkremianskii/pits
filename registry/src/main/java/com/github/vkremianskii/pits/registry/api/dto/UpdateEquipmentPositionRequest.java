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
}
