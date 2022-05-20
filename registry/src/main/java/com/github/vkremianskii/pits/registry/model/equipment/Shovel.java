package com.github.vkremianskii.pits.registry.model.equipment;

import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.Position;
import org.jetbrains.annotations.Nullable;

import static com.github.vkremianskii.pits.registry.model.EquipmentType.SHOVEL;

public class Shovel extends Equipment {

    public Shovel(int id, String name, @Nullable Position position) {
        super(id, name, SHOVEL, position);
    }
}
