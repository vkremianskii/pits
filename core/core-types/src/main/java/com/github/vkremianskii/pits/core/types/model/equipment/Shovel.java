package com.github.vkremianskii.pits.core.types.model.equipment;

import com.github.vkremianskii.pits.core.types.model.Equipment;
import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.EquipmentState;
import com.github.vkremianskii.pits.core.types.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

import static com.github.vkremianskii.pits.core.types.model.EquipmentType.SHOVEL;

public class Shovel extends Equipment {

    private static final Set<EquipmentState> STATES = Set.of();

    public final int loadRadius;

    public Shovel(EquipmentId id,
                  String name,
                  int loadRadius,
                  @Nullable EquipmentState state,
                  @Nullable Position position) {
        super(
            id,
            name,
            SHOVEL,
            state,
            position);
        this.loadRadius = loadRadius;
    }

    public static boolean isValidState(EquipmentState state) {
        return STATES.contains(state);
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
