package com.github.vkremianskii.pits.frontends.logic;

public interface MainViewPresenter {

    void sendEquipmentPosition(int equipmentId, double latitude, double longitude, int elevation);
    void sendEquipmentPayload(int equipmentId, int payload);

    void initializeFleet();
    void initializeLocations();

    void onWindowClosing();
    void onEquipmentSelected(int equipmentId);
}
