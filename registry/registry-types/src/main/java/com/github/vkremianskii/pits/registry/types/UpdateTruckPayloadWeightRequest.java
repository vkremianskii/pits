package com.github.vkremianskii.pits.registry.types;

import java.util.Objects;

public class UpdateTruckPayloadWeightRequest {
    private final int equipmentId;
    private final int weight;

    public UpdateTruckPayloadWeightRequest(int equipmentId, int weight) {
        this.equipmentId = equipmentId;
        this.weight = weight;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateTruckPayloadWeightRequest that = (UpdateTruckPayloadWeightRequest) o;
        return equipmentId == that.equipmentId && weight == that.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId, weight);
    }
}
