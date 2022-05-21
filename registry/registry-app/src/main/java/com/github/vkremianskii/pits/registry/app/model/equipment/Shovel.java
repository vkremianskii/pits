package com.github.vkremianskii.pits.registry.app.model.equipment;

import com.github.vkremianskii.pits.registry.app.model.Equipment;
import com.github.vkremianskii.pits.registry.app.model.EquipmentType;
import com.github.vkremianskii.pits.registry.app.model.Position;
import org.jetbrains.annotations.Nullable;

public class Shovel extends Equipment {

    public Shovel(int id,
                  String name,
                  @Nullable ShovelState state,
                  @Nullable Position position) {
        super(id, name, EquipmentType.SHOVEL, state, position);
    }
}
