package com.github.vkremianskii.pits.registry.model.equipment;

import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.EquipmentId;
import com.github.vkremianskii.pits.registry.model.EquipmentState;
import com.github.vkremianskii.pits.registry.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static com.github.vkremianskii.pits.registry.model.EquipmentType.DOZER;

public class Dozer extends Equipment {

    private static final Set<EquipmentState> STATES = Set.of();

    public Dozer(EquipmentId id,
                 String name,
                 @Nullable EquipmentState state,
                 @Nullable Position position) {
        super(
            id,
            name,
            DOZER,
            state,
            position);
    }

    public static boolean isValidState(EquipmentState state) {
        return STATES.contains(state);
    }
}
