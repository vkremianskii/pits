package com.github.vkremianskii.pits.frontends.grpc;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.EquipmentServiceBlockingStub;
import com.github.vkremianskii.pits.communicator.grpc.PayloadChanged;
import com.github.vkremianskii.pits.communicator.grpc.PositionChanged;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.newBlockingStub;
import static java.util.concurrent.TimeUnit.SECONDS;

public class EquipmentClient {
    private ManagedChannel channel;
    private EquipmentServiceBlockingStub stub;

    public void start() {
        channel = ManagedChannelBuilder.forAddress("localhost", 8083)
                .usePlaintext()
                .build();

        stub = newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown();
        channel.awaitTermination(5, SECONDS);
    }

    public void sendPositionChanged(int equipmentId, double latitude, double longitude, int elevation) {
        final var request = PositionChanged.newBuilder()
                .setEquipmentId(equipmentId)
                .setLatitude(latitude)
                .setLongitude(longitude)
                .setElevation(elevation)
                .build();

        final var response = stub.positionChanged(request);
    }

    public void sendPayloadChanged(int equipmentId, int payload) {
        final var request = PayloadChanged.newBuilder()
                .setEquipmentId(equipmentId)
                .setPayload(payload)
                .build();

        final var response = stub.payloadChanged(request);
    }
}
