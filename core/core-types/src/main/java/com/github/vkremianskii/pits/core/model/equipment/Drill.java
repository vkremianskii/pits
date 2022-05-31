package com.github.vkremianskii.pits.core.model.equipment;

import com.github.vkremianskii.pits.core.model.EquipmentId;
import com.github.vkremianskii.pits.core.model.Equipment;
import com.github.vkremianskii.pits.core.model.EquipmentState;
import com.github.vkremianskii.pits.core.model.Position;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static com.github.vkremianskii.pits.core.model.EquipmentType.DRILL;

public class Drill extends Equipment {

    private static final Set<EquipmentState> STATES = Set.of();

    public Drill(EquipmentId id,
                 String name,
                 @Nullable EquipmentState state,
                 @Nullable Position position) {
        super(
            id,
            name,
            DRILL,
            state,
            position);
    }

    public static boolean isValidState(EquipmentState state) {
        return STATES.contains(state);
    }
}
