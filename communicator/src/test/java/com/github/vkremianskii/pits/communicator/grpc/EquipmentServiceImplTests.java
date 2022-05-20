package com.github.vkremianskii.pits.communicator.grpc;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.EquipmentServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc.newBlockingStub;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

class EquipmentServiceImplTests {

    Server server;
    ManagedChannel channel;
    EquipmentServiceBlockingStub sut;

    @BeforeEach
    void setup() throws IOException {
        server = InProcessServerBuilder
                .forName("equipment")
                .directExecutor()
                .addService(new EquipmentServiceImpl())
                .build()
                .start();

        channel = InProcessChannelBuilder
                .forName("equipment")
                .directExecutor()
                .build();

        sut = newBlockingStub(channel);
    }

    @Test
    void should_send_position() {
        var request = PositionRequest.newBuilder()
                .setEquipmentId(1)
                .setLatitude(41.1494512)
                .setLongitude(-8.6107884)
                .setElevation(86)
                .build();

        var response = sut.position(request);

        assertThat(response).isNotNull();
    }

    @AfterEach
    void cleanup() throws InterruptedException {
        channel.shutdown().awaitTermination(5, SECONDS);
        server.shutdown().awaitTermination(5, SECONDS);
    }
}
