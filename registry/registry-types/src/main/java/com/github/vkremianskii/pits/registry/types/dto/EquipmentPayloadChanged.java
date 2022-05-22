package com.github.vkremianskii.pits.registry.types.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class EquipmentPayloadChanged {
    private final int equipmentId;
    private final int payload;

    @JsonCreator
    public EquipmentPayloadChanged(@JsonProperty("equipmentId") int equipmentId,
                                   @JsonProperty("payload") int payload) {
        this.equipmentId = equipmentId;
        this.payload = payload;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public int getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPayloadChanged that = (EquipmentPayloadChanged) o;
        return equipmentId == that.equipmentId && payload == that.payload;
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId, payload);
    }
}
