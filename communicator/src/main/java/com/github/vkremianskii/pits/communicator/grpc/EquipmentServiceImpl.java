package com.github.vkremianskii.pits.communicator.grpc;

import com.github.vkremianskii.pits.registry.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.types.dto.TruckPayloadWeightChanged;
import io.grpc.stub.StreamObserver;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static com.github.vkremianskii.pits.communicator.amqp.AmqpConfig.EXCHANGE_EQUIPMENT_POSITION;
import static com.github.vkremianskii.pits.communicator.amqp.AmqpConfig.EXCHANGE_TRUCK_PAYLOAD_WEIGHT;
import static java.util.Objects.requireNonNull;

public class EquipmentServiceImpl extends EquipmentServiceGrpc.EquipmentServiceImplBase {
    private final RabbitTemplate rabbitTemplate;

    public EquipmentServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = requireNonNull(rabbitTemplate);
    }

    @Override
    public void positionChanged(PositionChanged request, StreamObserver<PositionChangedResponse> responseObserver) {
        final var message = new EquipmentPositionChanged(
                request.getEquipmentId(),
                request.getLatitude(),
                request.getLongitude(),
                request.getElevation());
        rabbitTemplate.convertSendAndReceive(EXCHANGE_EQUIPMENT_POSITION, message);

        final var response = PositionChangedResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void payloadWeightChanged(PayloadWeightChanged request, StreamObserver<PayloadWeightChangedResponse> responseObserver) {
        final var message = new TruckPayloadWeightChanged(
                request.getEquipmentId(),
                request.getWeight());
        rabbitTemplate.convertSendAndReceive(EXCHANGE_TRUCK_PAYLOAD_WEIGHT, message);

        final var response = PayloadWeightChangedResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
