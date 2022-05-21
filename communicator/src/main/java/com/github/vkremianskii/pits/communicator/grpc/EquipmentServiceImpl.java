package com.github.vkremianskii.pits.communicator.grpc;

import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.UpdateEquipmentPositionRequest;
import com.github.vkremianskii.pits.registry.types.UpdateTruckPayloadWeightRequest;
import io.grpc.stub.StreamObserver;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;

import static com.github.vkremianskii.pits.communicator.amqp.AmqpConfig.EXCHANGE_EQUIPMENT_POSITION;
import static com.github.vkremianskii.pits.communicator.amqp.AmqpConfig.EXCHANGE_TRUCK_PAYLOAD_WEIGHT;
import static java.util.Objects.requireNonNull;

public class EquipmentServiceImpl extends EquipmentServiceGrpc.EquipmentServiceImplBase {
    private final RegistryClient registryClient;
    private final RabbitTemplate rabbitTemplate;

    public EquipmentServiceImpl(RegistryClient registryClient, RabbitTemplate rabbitTemplate) {
        this.registryClient = requireNonNull(registryClient);
        this.rabbitTemplate = requireNonNull(rabbitTemplate);
    }

    @Override
    public void positionChanged(PositionChanged request, StreamObserver<PositionChangedResponse> responseObserver) {
        registryClient.updateEquipmentPosition(
                request.getEquipmentId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getElevation());

        final var amqpRequest = new UpdateEquipmentPositionRequest(
                request.getEquipmentId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getElevation());
        rabbitTemplate.convertSendAndReceive(EXCHANGE_EQUIPMENT_POSITION, amqpRequest);

        final var response = PositionChangedResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void payloadWeightChanged(PayloadWeightChanged request, StreamObserver<PayloadWeightChangedResponse> responseObserver) {
        registryClient.updateTruckPayloadWeight(request.getEquipmentId(), request.getWeight());

        final var amqpRequest = new UpdateTruckPayloadWeightRequest(
                request.getEquipmentId(),
                request.getWeight());
        rabbitTemplate.convertSendAndReceive(EXCHANGE_TRUCK_PAYLOAD_WEIGHT, amqpRequest);

        final var response = PayloadWeightChangedResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
