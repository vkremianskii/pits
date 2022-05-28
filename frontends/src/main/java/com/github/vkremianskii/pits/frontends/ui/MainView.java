package com.github.vkremianskii.pits.frontends.ui;

import com.github.vkremianskii.pits.registry.types.model.Equipment;

import java.util.SortedMap;

public interface MainView {

    void refreshFleetControls(SortedMap<Integer, Equipment> equipmentById);
    void refreshEquipmentControls(Equipment equipment);

    void setInitializeFleetEnabled(boolean enabled);
}
