package com.github.vkremianskii.pits.core.types.model.equipment;

import com.github.vkremianskii.pits.core.types.model.EquipmentState;

public class DozerState extends EquipmentState {

    private static final DozerState[] values = new DozerState[0];

    protected DozerState(String value) {
        super(value);
    }

    public static DozerState valueOf(String name) {
        return valueOf(name, values);
    }
}
