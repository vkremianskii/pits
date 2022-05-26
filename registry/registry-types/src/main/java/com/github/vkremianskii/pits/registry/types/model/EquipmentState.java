package com.github.vkremianskii.pits.registry.types.model;

import java.util.Objects;

public abstract class EquipmentState {
    public final String name;

    protected EquipmentState(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EquipmentState)) return false;
        EquipmentState that = (EquipmentState) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }

    protected static <T extends EquipmentState> T valueOf(String name, T[] values) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        for (final var value : values) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No equipment state with name '" + name + "'");
    }
}
