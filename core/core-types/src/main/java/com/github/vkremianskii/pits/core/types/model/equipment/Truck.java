package com.github.vkremianskii.pits.core.types.model.equipment;

import com.github.vkremianskii.pits.core.types.model.Equipment;
import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.EquipmentState;
import com.github.vkremianskii.pits.core.types.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

import static com.github.vkremianskii.pits.core.types.model.EquipmentState.equipmentState;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.TRUCK;

public class Truck extends Equipment {

    public static final EquipmentState STATE_EMPTY = equipmentState("EMPTY");
    public static final EquipmentState STATE_WAIT_LOAD = equipmentState("WAIT_LOAD");
    public static final EquipmentState STATE_LOAD = equipmentState("LOAD");
    public static final EquipmentState STATE_HAUL = equipmentState("HAUL");
    public static final EquipmentState STATE_UNLOAD = equipmentState("UNLOAD");

    private static final Set<EquipmentState> STATES = Set.of(
        STATE_EMPTY,
        STATE_WAIT_LOAD,
        STATE_LOAD,
        STATE_HAUL,
        STATE_UNLOAD);

    public final Integer payload;

    public Truck(EquipmentId id,
                 String name,
                 @Nullable EquipmentState state,
                 @Nullable Position position,
                 @Nullable Integer payload) {
        super(
            id,
            name,
            TRUCK,
            state,
            position);
        this.payload = payload;
    }

    public static boolean isValidState(EquipmentState state) {
        return STATES.contains(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Truck truck = (Truck) o;
        return Objects.equals(payload, truck.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), payload);
    }

    @Override
    public String toString() {
        return "Truck{" +
            "payload=" + payload +
            ", id=" + id +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", state=" + state +
            ", position=" + position +
            '}';
    }
}
