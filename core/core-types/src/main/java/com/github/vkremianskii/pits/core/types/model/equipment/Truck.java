package com.github.vkremianskii.pits.core.types.model.equipment;

import com.github.vkremianskii.pits.core.types.model.Equipment;
import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.github.vkremianskii.pits.core.types.model.EquipmentType.TRUCK;

public class Truck extends Equipment {

    public final Integer payload;

    public Truck(EquipmentId id,
                 String name,
                 @Nullable TruckState state,
                 @Nullable Position position,
                 @Nullable Integer payload) {
        super(id, name, TRUCK, state, position);
        this.payload = payload;
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
