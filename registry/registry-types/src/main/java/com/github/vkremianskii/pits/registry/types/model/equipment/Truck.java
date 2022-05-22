package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Truck extends Equipment {
    private final Integer payload;

    public Truck(int id,
                 String name,
                 @Nullable TruckState state,
                 @Nullable Position position,
                 @Nullable Integer payload) {
        super(id, name, EquipmentType.TRUCK, state, position);
        this.payload = payload;
    }

    public Integer getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Truck)) return false;
        if (!super.equals(o)) return false;
        Truck truck = (Truck) o;
        return Objects.equals(payload, truck.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), payload);
    }
}
