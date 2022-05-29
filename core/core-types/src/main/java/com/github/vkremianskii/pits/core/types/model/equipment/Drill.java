package com.github.vkremianskii.pits.core.types.model.equipment;

import com.github.vkremianskii.pits.core.types.model.Equipment;
import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.Position;
import org.jetbrains.annotations.Nullable;

import static com.github.vkremianskii.pits.core.types.model.EquipmentType.DRILL;

public class Drill extends Equipment {

    public Drill(EquipmentId id,
                 String name,
                 @Nullable DrillState state,
                 @Nullable Position position) {
        super(id, name, DRILL, state, position);
    }
}
