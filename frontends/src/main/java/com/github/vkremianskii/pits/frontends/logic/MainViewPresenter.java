package com.github.vkremianskii.pits.frontends.logic;

import java.util.UUID;

public interface MainViewPresenter {

    void sendEquipmentPosition(UUID equipmentId, double latitude, double longitude, int elevation);
    void sendEquipmentPayload(UUID equipmentId, int payload);

    void initializeFleet();
    void initializeLocations();

    void onWindowClosing();
    void onEquipmentSelected(UUID equipmentId);
}
