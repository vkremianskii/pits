package com.github.vkremianskii.pits.core.model;

import com.github.vkremianskii.pits.core.Microtype;

public class EquipmentState extends Microtype<String> {

    private EquipmentState(String value) {
        super(value);
    }

    public static EquipmentState equipmentState(String value) {
        return new EquipmentState(value);
    }

    public static EquipmentState valueOf(String value) {
        return equipmentState(value);
    }
}
