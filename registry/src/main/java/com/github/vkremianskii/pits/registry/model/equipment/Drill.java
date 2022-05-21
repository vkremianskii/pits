package com.github.vkremianskii.pits.registry.model.equipment;

import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.Position;
import org.jetbrains.annotations.Nullable;

import static com.github.vkremianskii.pits.registry.model.EquipmentType.DRILL;

public class Drill extends Equipment {

    public Drill(int id,
                 String name,
                 @Nullable DrillState state,
                 @Nullable Position position) {
        super(id, name, DRILL, state, position);
    }
}
