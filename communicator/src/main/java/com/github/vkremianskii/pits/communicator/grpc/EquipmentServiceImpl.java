package com.github.vkremianskii.pits.communicator.grpc;

import com.github.vkremianskii.pits.communicator.integration.RegistryService;
import io.grpc.stub.StreamObserver;

import static java.util.Objects.requireNonNull;

public class EquipmentServiceImpl extends EquipmentServiceGrpc.EquipmentServiceImplBase {
    private final RegistryService registryService;

    public EquipmentServiceImpl(RegistryService registryService) {
        this.registryService = requireNonNull(registryService);
    }

    @Override
    public void updatePosition(UpdatePositionRequest request, StreamObserver<UpdatePositionResponse> responseObserver) {
        registryService.updateEquipmentPosition(
                request.getEquipmentId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getElevation());

        final var response = UpdatePositionResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
