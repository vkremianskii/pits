package com.github.vkremianskii.pits.communicator;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceImpl;
import com.github.vkremianskii.pits.communicator.grpc.GrpcProperties;
import com.github.vkremianskii.pits.communicator.integration.RegistryService;
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
    private final RegistryService registryService;

    private Server server;

    public GrpcServer(GrpcProperties grpcProperties,
                      RegistryService registryService) {
        this.grpcProperties = requireNonNull(grpcProperties);
        this.registryService = requireNonNull(registryService);
    }

    @EventListener
    public void handleContextStart(ContextRefreshedEvent event) throws IOException {
        server = ServerBuilder.forPort(grpcProperties.getPort())
                .addService(new EquipmentServiceImpl(registryService))
                .build();

        server.start();
    }

    @EventListener
    public void handleContextStop(ContextStoppedEvent event) throws InterruptedException {
        server.shutdown();
        server.awaitTermination();
    }
}
