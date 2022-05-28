package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DOZER;

public class Dozer extends Equipment {

    public Dozer(UUID id,
                 String name,
                 @Nullable DozerState state,
                 @Nullable Position position) {
        super(id, name, DOZER, state, position);
    }
}
