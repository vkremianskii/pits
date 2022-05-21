package com.github.vkremianskii.pits.communicator.grpc;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.EquipmentServiceBlockingStub;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.UpdateEquipmentPositionRequest;
import com.github.vkremianskii.pits.registry.types.UpdateTruckPayloadWeightRequest;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

import static com.github.vkremianskii.pits.communicator.amqp.AmqpConfig.EXCHANGE_EQUIPMENT_POSITION;
import static com.github.vkremianskii.pits.communicator.amqp.AmqpConfig.EXCHANGE_TRUCK_PAYLOAD_WEIGHT;
import static com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.newBlockingStub;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EquipmentServiceImplTests {
    RegistryClient registryClient = mock(RegistryClient.class);
    RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

    Server server;
    ManagedChannel channel;
    EquipmentServiceBlockingStub sut;

    @BeforeEach
    void setup() throws IOException {
        server = InProcessServerBuilder
                .forName("equipment")
                .directExecutor()
                .addService(new EquipmentServiceImpl(registryClient, rabbitTemplate))
                .build()
                .start();

        channel = InProcessChannelBuilder
                .forName("equipment")
                .directExecutor()
                .build();

        sut = newBlockingStub(channel);
    }

    @Test
    void should_receive_position_and_update_in_registry() {
        // given
        var request = PositionChanged.newBuilder()
                .setEquipmentId(1)
                .setLatitude(41.1494512)
                .setLongitude(-8.6107884)
                .setElevation(86)
                .build();
        var expectedAmqpRequest = new UpdateEquipmentPositionRequest(
                1,
                41.1494512,
                -8.6107884,
                86);

        // when
        var response = sut.positionChanged(request);

        // then
        assertThat(response).isNotNull();
        verify(registryClient).updateEquipmentPosition(1, 41.1494512, -8.6107884, 86);
        verify(rabbitTemplate).convertSendAndReceive(eq(EXCHANGE_EQUIPMENT_POSITION), eq(expectedAmqpRequest));
    }

    @Test
    void should_receive_payload_weight_and_update_in_registry() {
        // given
        var request = PayloadWeightChanged.newBuilder()
                .setEquipmentId(1)
                .setWeight(10)
                .build();
        var expectedAmqpRequest = new UpdateTruckPayloadWeightRequest(1, 10);

        // when
        var response = sut.payloadWeightChanged(request);

        // then
        assertThat(response).isNotNull();
        verify(registryClient).updateTruckPayloadWeight(1, 10);
        verify(rabbitTemplate).convertSendAndReceive(eq(EXCHANGE_TRUCK_PAYLOAD_WEIGHT), eq(expectedAmqpRequest));
    }

    @AfterEach
    void cleanup() throws InterruptedException {
        channel.shutdown().awaitTermination(5, SECONDS);
        server.shutdown().awaitTermination();
    }
}
