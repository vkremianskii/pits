package com.github.vkremianskii.pits.core.types.model.equipment;

import com.github.vkremianskii.pits.core.types.model.EquipmentState;

public class ShovelState extends EquipmentState {

    private static final ShovelState[] values = new ShovelState[0];

    protected ShovelState(String value) {
        super(value);
    }

    public static ShovelState valueOf(String name) {
        return valueOf(name, values);
    }
}
