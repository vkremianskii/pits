package com.github.vkremianskii.pits.core.types.model.equipment;

import com.github.vkremianskii.pits.core.types.model.EquipmentState;

public class DrillState extends EquipmentState {

    private static final DrillState[] values = new DrillState[0];

    protected DrillState(String value) {
        super(value);
    }

    public static DrillState valueOf(String name) {
        return valueOf(name, values);
    }
}
