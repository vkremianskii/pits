package com.github.vkremianskii.pits.registry.app.model.equipment;

import com.github.vkremianskii.pits.registry.app.model.Equipment;
import com.github.vkremianskii.pits.registry.app.model.EquipmentType;
import com.github.vkremianskii.pits.registry.app.model.Position;
import org.jetbrains.annotations.Nullable;

public class Truck extends Equipment {

    public Truck(int id,
                 String name,
                 @Nullable TruckState state,
                 @Nullable Position position) {
        super(id, name, EquipmentType.TRUCK, state, position);
    }
}
