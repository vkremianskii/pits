package com.github.vkremianskii.pits.frontends.logic;

import com.github.vkremianskii.pits.core.model.EquipmentId;

public interface MainViewPresenter {

    void sendEquipmentPosition(EquipmentId equipmentId, double latitude, double longitude, int elevation);
    void sendEquipmentPayload(EquipmentId equipmentId, int payload);

    void initializeFleet();
    void initializeLocations();

    void onWindowClosing();
    void onEquipmentSelected(EquipmentId equipmentId);
}
