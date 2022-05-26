package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Shovel extends Equipment {
    public final int loadRadius;

    public Shovel(int id,
                  String name,
                  int loadRadius,
                  @Nullable ShovelState state,
                  @Nullable Position position) {
        super(id, name, EquipmentType.SHOVEL, state, position);
        this.loadRadius = loadRadius;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Shovel shovel = (Shovel) o;
        return loadRadius == shovel.loadRadius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loadRadius);
    }

    @Override
    public String toString() {
        return "Shovel{" +
                "loadRadius=" + loadRadius +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", state=" + state +
                ", position=" + position +
                '}';
    }
}
