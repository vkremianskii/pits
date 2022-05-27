package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.EquipmentState;

public class DrillState extends EquipmentState {

    private static final DrillState[] values = new DrillState[0];

    protected DrillState(String value) {
        super(value);
    }

    public static DrillState valueOf(String name) {
        return valueOf(name, values);
    }
}
