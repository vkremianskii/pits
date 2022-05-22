package com.github.vkremianskii.pits.communicator.app.grpc;

import com.github.vkremianskii.pits.communicator.grpc.*;
import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.EquipmentServiceImplBase;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPositionChanged;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentPayloadChanged;
import io.grpc.stub.StreamObserver;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static com.github.vkremianskii.pits.communicator.app.amqp.AmqpConfig.EXCHANGE_EQUIPMENT_POSITION;
import static com.github.vkremianskii.pits.communicator.app.amqp.AmqpConfig.EXCHANGE_EQUIPMENT_PAYLOAD;
import static java.util.Objects.requireNonNull;

public class EquipmentServiceImpl extends EquipmentServiceImplBase {
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
        rabbitTemplate.convertSendAndReceive(EXCHANGE_EQUIPMENT_POSITION, "", message);

        final var response = PositionChangedResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void payloadChanged(PayloadChanged request, StreamObserver<PayloadChangedResponse> responseObserver) {
        final var message = new EquipmentPayloadChanged(
                request.getEquipmentId(),
                request.getPayload());
        rabbitTemplate.convertSendAndReceive(EXCHANGE_EQUIPMENT_PAYLOAD, "", message);

        final var response = PayloadChangedResponse.newBuilder().build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
