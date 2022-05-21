package com.github.vkremianskii.pits.registry.app.model.equipment;

import com.github.vkremianskii.pits.registry.app.model.Equipment;
import com.github.vkremianskii.pits.registry.app.model.EquipmentType;
import com.github.vkremianskii.pits.registry.app.model.Position;
import org.jetbrains.annotations.Nullable;

public class Truck extends Equipment {
    private final Integer payloadWeight;

    public Truck(int id,
                 String name,
                 @Nullable TruckState state,
                 @Nullable Position position,
                 @Nullable Integer payloadWeight) {
        super(id, name, EquipmentType.TRUCK, state, position);
        this.payloadWeight = payloadWeight;
    }

    public Integer payloadWeight() {
        return payloadWeight;
    }
}
