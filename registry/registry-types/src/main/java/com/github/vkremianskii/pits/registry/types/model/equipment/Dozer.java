package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.jetbrains.annotations.Nullable;

public class Dozer extends Equipment {

    public Dozer(int id,
                 String name,
                 @Nullable DozerState state,
                 @Nullable Position position) {
        super(id, name, EquipmentType.DOZER, state, position);
    }
}
