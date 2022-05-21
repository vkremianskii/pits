package com.github.vkremianskii.pits.registry.model.equipment;

import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.Position;
import org.jetbrains.annotations.Nullable;

import static com.github.vkremianskii.pits.registry.model.EquipmentType.DOZER;

public class Dozer extends Equipment {

    public Dozer(int id,
                 String name,
                 @Nullable DozerState state,
                 @Nullable Position position) {
        super(id, name, DOZER, state, position);
    }
}
