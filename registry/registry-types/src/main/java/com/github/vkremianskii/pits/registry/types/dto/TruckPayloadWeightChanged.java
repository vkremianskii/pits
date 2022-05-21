package com.github.vkremianskii.pits.registry.types.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TruckPayloadWeightChanged {
    private final int equipmentId;
    private final int weight;

    @JsonCreator
    public TruckPayloadWeightChanged(@JsonProperty("equipmentId") int equipmentId,
                                     @JsonProperty("weight") int weight) {
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
        TruckPayloadWeightChanged that = (TruckPayloadWeightChanged) o;
        return equipmentId == that.equipmentId && weight == that.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId, weight);
    }
}
