package com.github.vkremianskii.pits.registry.app.model.equipment;

import com.github.vkremianskii.pits.registry.app.model.EquipmentState;

public class ShovelState extends EquipmentState {
    private static final ShovelState[] values = new ShovelState[0];

    protected ShovelState(String value) {
        super(value);
    }

    public static ShovelState[] values() {
        return values;
    }

    public static ShovelState valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        for (var value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No shovel state with name '" + name + "'");
    }
}
