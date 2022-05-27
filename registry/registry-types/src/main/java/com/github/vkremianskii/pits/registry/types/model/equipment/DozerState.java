package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.EquipmentState;

public class DozerState extends EquipmentState {

    private static final DozerState[] values = new DozerState[0];

    protected DozerState(String value) {
        super(value);
    }

    public static DozerState valueOf(String name) {
        return valueOf(name, values);
    }
}
