package com.github.vkremianskii.pits.registry.app.model.equipment;

import com.github.vkremianskii.pits.registry.app.model.EquipmentState;

public class DozerState extends EquipmentState {
    private static final DozerState[] values = new DozerState[0];

    protected DozerState(String value) {
        super(value);
    }

    public static DozerState[] values() {
        return values;
    }

    public static DozerState valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        for (var value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No dozer state with name '" + name + "'");
    }
}
