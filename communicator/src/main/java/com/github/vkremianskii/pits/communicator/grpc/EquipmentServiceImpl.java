package com.github.vkremianskii.pits.communicator.grpc;

import com.github.vkremianskii.pits.registry.client.RegistryClient;
import io.grpc.stub.StreamObserver;

import static java.util.Objects.requireNonNull;

public class EquipmentServiceImpl extends EquipmentServiceGrpc.EquipmentServiceImplBase {
    private final RegistryClient registryClient;

    public EquipmentServiceImpl(RegistryClient registryClient) {
        this.registryClient = requireNonNull(registryClient);
    }

    @Override
    public void positionChanged(PositionChanged request, StreamObserver<PositionChangedResponse> responseObserver) {
        registryClient.updateEquipmentPosition(
                request.getEquipmentId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getElevation());

        final var response = PositionChangedResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void bodyWeightChanged(BodyWeightChanged request, StreamObserver<BodyWeightChangedResponse> responseObserver) {
        final var response = BodyWeightChangedResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
