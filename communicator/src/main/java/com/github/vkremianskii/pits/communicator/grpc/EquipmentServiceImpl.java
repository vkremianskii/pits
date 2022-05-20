package com.github.vkremianskii.pits.communicator.grpc;

import io.grpc.stub.StreamObserver;

public class EquipmentServiceImpl extends EquipmentServiceGrpc.EquipmentServiceImplBase {

    @Override
    public void position(PositionRequest request, StreamObserver<PositionResponse> responseObserver) {
        responseObserver.onNext(PositionResponse.newBuilder().build());
        responseObserver.onCompleted();
    }
}
