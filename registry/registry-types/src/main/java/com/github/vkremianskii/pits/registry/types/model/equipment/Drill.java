package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DRILL;

public class Drill extends Equipment {

    public Drill(UUID id,
                 String name,
                 @Nullable DrillState state,
                 @Nullable Position position) {
        super(id, name, DRILL, state, position);
    }
}
