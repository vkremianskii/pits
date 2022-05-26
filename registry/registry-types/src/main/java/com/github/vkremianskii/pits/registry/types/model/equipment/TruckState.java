package com.github.vkremianskii.pits.registry.types.model.equipment;

import com.github.vkremianskii.pits.registry.types.model.EquipmentState;

public class TruckState extends EquipmentState {
    public static final TruckState EMPTY = new TruckState("EMPTY");
    public static final TruckState WAIT_LOAD = new TruckState("WAIT_LOAD");
    public static final TruckState LOAD = new TruckState("LOAD");
    public static final TruckState HAUL = new TruckState("HAUL");
    public static final TruckState UNLOAD = new TruckState("UNLOAD");

    private static final TruckState[] values = new TruckState[]{EMPTY, WAIT_LOAD, LOAD, HAUL, UNLOAD};

    protected TruckState(String value) {
        super(value);
    }

    public static TruckState valueOf(String name) {
        return valueOf(name, values);
    }
}
