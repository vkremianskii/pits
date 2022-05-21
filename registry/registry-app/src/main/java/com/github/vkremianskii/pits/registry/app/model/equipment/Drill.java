package com.github.vkremianskii.pits.registry.app.model.equipment;

import com.github.vkremianskii.pits.registry.app.model.Equipment;
import com.github.vkremianskii.pits.registry.app.model.EquipmentType;
import com.github.vkremianskii.pits.registry.app.model.Position;
import org.jetbrains.annotations.Nullable;

public class Drill extends Equipment {

    public Drill(int id,
                 String name,
                 @Nullable DrillState state,
                 @Nullable Position position) {
        super(id, name, EquipmentType.DRILL, state, position);
    }
}
