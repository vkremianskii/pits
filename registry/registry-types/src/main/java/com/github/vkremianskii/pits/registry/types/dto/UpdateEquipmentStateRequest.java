package com.github.vkremianskii.pits.registry.types.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class UpdateEquipmentStateRequest {
    private final EquipmentState state;

    @JsonCreator
    public UpdateEquipmentStateRequest(@JsonProperty("state") EquipmentState state) {
        this.state = requireNonNull(state);
    }

    public EquipmentState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdateEquipmentStateRequest)) return false;
        UpdateEquipmentStateRequest that = (UpdateEquipmentStateRequest) o;
        return Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }
}
