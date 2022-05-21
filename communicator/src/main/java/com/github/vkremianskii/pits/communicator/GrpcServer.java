package com.github.vkremianskii.pits.communicator;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceImpl;
import com.github.vkremianskii.pits.communicator.grpc.GrpcProperties;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@Component
public class GrpcServer {
    private final GrpcProperties grpcProperties;
    private final RegistryClient registryClient;

    private Server server;

    public GrpcServer(GrpcProperties grpcProperties,
                      RegistryClient registryClient) {
        this.grpcProperties = requireNonNull(grpcProperties);
        this.registryClient = requireNonNull(registryClient);
    }

    @EventListener
    public void handleContextStart(ContextRefreshedEvent event) throws IOException {
        server = ServerBuilder.forPort(grpcProperties.getPort())
                .addService(new EquipmentServiceImpl(registryClient))
                .build();

        server.start();
    }

    @EventListener
    public void handleContextStop(ContextStoppedEvent event) throws InterruptedException {
        server.shutdown();
        server.awaitTermination();
    }
}
