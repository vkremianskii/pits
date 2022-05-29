package com.github.vkremianskii.pits.communicator.app.grpc;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.EquipmentServiceBlockingStub;
import com.github.vkremianskii.pits.communicator.grpc.PayloadChanged;
import com.github.vkremianskii.pits.communicator.grpc.PositionChanged;
import com.github.vkremianskii.pits.core.types.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.core.types.dto.EquipmentPositionChanged;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

import static com.github.vkremianskii.pits.communicator.app.amqp.AmqpConfig.EXCHANGE_EQUIPMENT_PAYLOAD;
import static com.github.vkremianskii.pits.communicator.app.amqp.AmqpConfig.EXCHANGE_EQUIPMENT_POSITION;
import static com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.newBlockingStub;
import static com.github.vkremianskii.pits.core.types.TestEquipment.randomEquipmentId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EquipmentServiceImplTests {

    RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

    Server server;
    ManagedChannel channel;
    EquipmentServiceBlockingStub sut;

    @BeforeEach
    void setup() throws IOException {
        server = InProcessServerBuilder
            .forName("equipment")
            .directExecutor()
            .addService(new EquipmentServiceImpl(rabbitTemplate))
            .build()
            .start();

        channel = InProcessChannelBuilder
            .forName("equipment")
            .directExecutor()
            .build();

        sut = newBlockingStub(channel);
    }

    @Test
    void should_listen_to_position_changed_and_forward_to_rabbitmq() {
        // given
        var equipmentId = randomEquipmentId();
        var request = PositionChanged.newBuilder()
            .setEquipmentId(equipmentId.toString())
            .setLatitude(41.1494512)
            .setLongitude(-8.6107884)
            .setElevation(86)
            .build();
        var expectedMessage = new EquipmentPositionChanged(
            equipmentId,
            41.1494512,
            -8.6107884,
            86);

        // when
        var response = sut.positionChanged(request);

        // then
        assertThat(response).isNotNull();
        verify(rabbitTemplate).convertSendAndReceive(eq(EXCHANGE_EQUIPMENT_POSITION), eq(""), eq(expectedMessage));
    }

    @Test
    void should_listen_to_payload_changed_and_forward_to_rabbitmq() {
        // given
        var equipmentId = randomEquipmentId();
        var request = PayloadChanged.newBuilder()
            .setEquipmentId(equipmentId.toString())
            .setPayload(10)
            .build();
        var expectedMessage = new EquipmentPayloadChanged(equipmentId, 10);

        // when
        var response = sut.payloadChanged(request);

        // then
        assertThat(response).isNotNull();
        verify(rabbitTemplate).convertSendAndReceive(eq(EXCHANGE_EQUIPMENT_PAYLOAD), eq(""), eq(expectedMessage));
    }

    @AfterEach
    void cleanup() throws InterruptedException {
        channel.shutdown().awaitTermination(5, SECONDS);
        server.shutdown().awaitTermination();
    }
}
