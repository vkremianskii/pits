package com.github.vkremianskii.pits.frontends.ui;

import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.Location;

import java.util.List;
import java.util.SortedMap;
import java.util.UUID;

public interface MainView {

    void refreshFleet(SortedMap<UUID, Equipment> equipmentById);
    void refreshEquipment(Equipment equipment);
    void refreshLocations(List<Location> locations);

    void setInitializeFleetEnabled(boolean enabled);
    void setInitializeLocationsEnabled(boolean enabled);
}
