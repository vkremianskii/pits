package com.github.vkremianskii.pits.registry.model.equipment;

import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.Position;
import org.jetbrains.annotations.Nullable;

import static com.github.vkremianskii.pits.registry.model.EquipmentType.TRUCK;

public class Truck extends Equipment {

    public Truck(int id,
                 String name,
                 @Nullable TruckState state,
                 @Nullable Position position) {
        super(id, name, TRUCK, state, position);
    }
}
