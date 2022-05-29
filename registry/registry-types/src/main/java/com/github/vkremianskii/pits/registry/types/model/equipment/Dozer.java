package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.jetbrains.annotations.Nullable;

import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DOZER;

public class Dozer extends Equipment {

    public Dozer(EquipmentId id,
                 String name,
                 @Nullable DozerState state,
                 @Nullable Position position) {
        super(id, name, DOZER, state, position);
    }
}
