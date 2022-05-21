package com.github.vkremianskii.pits.registry.model;

import org.jetbrains.annotations.Nullable;

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
}
