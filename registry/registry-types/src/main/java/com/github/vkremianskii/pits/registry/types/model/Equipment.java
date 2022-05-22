package com.github.vkremianskii.pits.registry.types.model;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Equipment {
    private final int id;
    private final String name;
    private final EquipmentType type;
    private final EquipmentState state;
    private final Position position;

    protected Equipment(int id,
                         String name,
                         EquipmentType type,
                         @Nullable EquipmentState state,
                         @Nullable Position position) {
        this.id = id;
        this.name = requireNonNull(name);
        this.type = requireNonNull(type);
        this.state = state;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EquipmentType getType() {
        return type;
    }

    @Nullable
    public EquipmentState getState() {
        return state;
    }

    @Nullable
    public Position getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        Equipment equipment = (Equipment) o;
        return id == equipment.id &&
                Objects.equals(name, equipment.name) &&
                type == equipment.type &&
                Objects.equals(state, equipment.state) &&
                Objects.equals(position, equipment.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, state, position);
    }
}
