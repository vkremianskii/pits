package com.github.vkremianskii.pits.registry.model.equipment;

import com.github.vkremianskii.pits.registry.model.EquipmentState;

public class TruckState extends EquipmentState {
    public static final TruckState EMPTY = new TruckState("empty");
    public static final TruckState LOAD = new TruckState("load");
    public static final TruckState HAUL = new TruckState("haul");
    public static final TruckState UNLOAD = new TruckState("unload");

    private static final TruckState[] values = new TruckState[]{EMPTY, LOAD, HAUL, UNLOAD};

    protected TruckState(String value) {
        super(value);
    }

    public static TruckState[] values() {
        return values;
    }

    public static TruckState valueOf(String name) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        for (var value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("No truck state with name '" + name + "'");
    }
}
