package com.github.vkremianskii.pits.frontends.ui;

import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.EquipmentId;
import com.github.vkremianskii.pits.registry.model.Location;

import java.util.List;
import java.util.SortedMap;

public interface MainView {

    void refreshFleet(SortedMap<EquipmentId, Equipment> equipmentById);
    void refreshEquipment(Equipment equipment);
    void refreshLocations(List<Location> locations);

    void setInitializeFleetEnabled(boolean enabled);
    void setInitializeLocationsEnabled(boolean enabled);
}
