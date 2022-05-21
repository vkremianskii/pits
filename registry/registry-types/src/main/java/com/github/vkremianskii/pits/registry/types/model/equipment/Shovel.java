package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.jetbrains.annotations.Nullable;

public class Shovel extends Equipment {

    public Shovel(int id,
                  String name,
                  @Nullable ShovelState state,
                  @Nullable Position position) {
        super(id, name, EquipmentType.SHOVEL, state, position);
    }
}
