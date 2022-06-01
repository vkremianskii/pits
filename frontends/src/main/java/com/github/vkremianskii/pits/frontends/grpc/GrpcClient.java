package com.github.vkremianskii.pits.frontends.grpc;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.EquipmentServiceFutureStub;
import com.github.vkremianskii.pits.communicator.grpc.PayloadChanged;
import com.github.vkremianskii.pits.communicator.grpc.PayloadChangedResponse;
import com.github.vkremianskii.pits.communicator.grpc.PositionChanged;
import com.github.vkremianskii.pits.communicator.grpc.PositionChangedResponse;
import com.github.vkremianskii.pits.registry.model.EquipmentId;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;

import static com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.newFutureStub;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;

public class GrpcClient {

    private final ExecutorService executor = newSingleThreadExecutor();

    private ManagedChannel channel;
    private EquipmentServiceFutureStub equipmentStub;

    public void start() {
        channel = ManagedChannelBuilder.forAddress("localhost", 8084)
            .usePlaintext()
            .build();

        equipmentStub = newFutureStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown();
        channel.awaitTermination(5, SECONDS);
    }

    public Mono<PositionChangedResponse> sendPositionChanged(EquipmentId equipmentId, double latitude, double longitude, int elevation) {
        final var request = PositionChanged.newBuilder()
            .setEquipmentId(equipmentId.toString())
            .setLatitude(latitude)
            .setLongitude(longitude)
            .setElevation(elevation)
            .build();

        return monoFromListenableFuture(equipmentStub.positionChanged(request));
    }

    public Mono<PayloadChangedResponse> sendPayloadChanged(EquipmentId equipmentId, int payload) {
        final var request = PayloadChanged.newBuilder()
            .setEquipmentId(equipmentId.toString())
            .setPayload(payload)
            .build();

        return monoFromListenableFuture(equipmentStub.payloadChanged(request));
    }

    private <T> Mono<T> monoFromListenableFuture(ListenableFuture<T> future) {
        return Mono.create(sink ->
            Futures.addCallback(future, new FutureCallback<T>() {
                @Override
                public void onSuccess(T result) {
                    sink.success(result);
                }

                @Override
                public void onFailure(Throwable t) {
                    sink.error(t);
                }
            }, executor));
    }
}
