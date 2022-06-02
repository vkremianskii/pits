package com.github.vkremianskii.pits.communicator.grpc;

import com.github.vkremianskii.pits.communicator.amqp.AmqpConfig;
import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.EquipmentServiceBlockingStub;
import com.github.vkremianskii.pits.registry.dto.EquipmentPayloadChanged;
import com.github.vkremianskii.pits.registry.dto.EquipmentPositionChanged;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

import static com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.newBlockingStub;
import static com.github.vkremianskii.pits.registry.TestEquipment.randomEquipmentId;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
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

        // when
        var response = sut.positionChanged(request);

        // then
        assertThat(response).isNotNull();
        var captor = ArgumentCaptor.forClass(EquipmentPositionChanged.class);
        verify(rabbitTemplate).convertSendAndReceive(
            eq(AmqpConfig.EXCHANGE_EQUIPMENT_POSITION),
            eq(""),
            captor.capture());
        var message = captor.getValue();
        assertThat(message.equipmentId()).isEqualTo(equipmentId);
        assertThat(message.latitude()).isEqualTo(41.1494512);
        assertThat(message.longitude()).isEqualTo(-8.6107884);
        assertThat(message.elevation()).isEqualTo(86);
        assertThat(message.receiveTimestamp()).isCloseTo(now(), within(1, MINUTES));
    }

    @Test
    void should_listen_to_payload_changed_and_forward_to_rabbitmq() {
        // given
        var equipmentId = randomEquipmentId();
        var request = PayloadChanged.newBuilder()
            .setEquipmentId(equipmentId.toString())
            .setPayload(10)
            .build();

        // when
        var response = sut.payloadChanged(request);

        // then
        assertThat(response).isNotNull();
        var captor = ArgumentCaptor.forClass(EquipmentPayloadChanged.class);
        verify(rabbitTemplate).convertSendAndReceive(
            eq(AmqpConfig.EXCHANGE_EQUIPMENT_PAYLOAD),
            eq(""),
            captor.capture());
        var message = captor.getValue();
        assertThat(message.equipmentId()).isEqualTo(equipmentId);
        assertThat(message.payload()).isEqualTo(10);
        assertThat(message.receiveTimestamp()).isCloseTo(now(), within(1, MINUTES));
    }

    @AfterEach
    void cleanup() throws InterruptedException {
        channel.shutdown().awaitTermination(5, SECONDS);
        server.shutdown().awaitTermination();
    }
}
