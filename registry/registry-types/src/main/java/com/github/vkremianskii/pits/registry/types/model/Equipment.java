package com.github.vkremianskii.pits.registry.types.model;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class Equipment {

    public final UUID id;
    public final String name;
    public final EquipmentType type;
    public final EquipmentState state;
    public final Position position;

    protected Equipment(UUID id,
                        String name,
                        EquipmentType type,
                        @Nullable EquipmentState state,
                        @Nullable Position position) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.type = requireNonNull(type);
        this.state = state;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        Equipment equipment = (Equipment) o;
        return id.equals(equipment.id) && Objects.equals(name, equipment.name) && type == equipment.type && Objects.equals(state, equipment.state) && Objects.equals(position, equipment.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, state, position);
    }

    @Override
    public String toString() {
        return "Equipment{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", state=" + state +
            ", position=" + position +
            '}';
    }
}
