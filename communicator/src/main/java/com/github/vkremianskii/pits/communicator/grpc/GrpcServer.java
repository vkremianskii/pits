package com.github.vkremianskii.pits.communicator.grpc;

import com.github.vkremianskii.pits.registry.client.RegistryClient;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final RabbitTemplate rabbitTemplate;

    private Server server;

    public GrpcServer(GrpcProperties grpcProperties,
                      RegistryClient registryClient,
                      RabbitTemplate rabbitTemplate) {
        this.grpcProperties = requireNonNull(grpcProperties);
        this.registryClient = requireNonNull(registryClient);
        this.rabbitTemplate = requireNonNull(rabbitTemplate);
    }

    @EventListener
    public void handleContextStart(ContextRefreshedEvent event) throws IOException {
        server = ServerBuilder.forPort(grpcProperties.getPort())
                .addService(new EquipmentServiceImpl(registryClient, rabbitTemplate))
                .build();

        server.start();
    }

    @EventListener
    public void handleContextStop(ContextStoppedEvent event) throws InterruptedException {
        server.shutdown();
        server.awaitTermination();
    }
}
