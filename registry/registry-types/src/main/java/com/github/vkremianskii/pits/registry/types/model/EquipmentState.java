package com.github.vkremianskii.pits.registry.types.model;

import java.util.Objects;

public abstract class EquipmentState {
    private final String name;

    protected EquipmentState(String name) {
        this.name = name;
    }

    public String name() {
        return name;
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
        return "EquipmentState{" +
                "name='" + name + '\'' +
                '}';
    }
}
