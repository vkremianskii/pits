package com.github.vkremianskii.pits.registry.types;

public class UpdateTruckPayloadWeightRequest {
    private final int weight;

    public UpdateTruckPayloadWeightRequest(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
