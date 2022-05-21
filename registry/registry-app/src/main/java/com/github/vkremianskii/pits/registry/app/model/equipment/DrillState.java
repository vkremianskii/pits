package com.github.vkremianskii.pits.registry.app.model.equipment;

import com.github.vkremianskii.pits.registry.app.model.EquipmentState;

public class DrillState extends EquipmentState {
    private static final DrillState[] values = new DrillState[0];

    protected DrillState(String value) {
        super(value);
    }

    public static DrillState[] values() {
        return values;
    }

    public static DrillState valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        for (var value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No drill state with name '" + name + "'");
    }
}
