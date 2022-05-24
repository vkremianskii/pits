package com.github.vkremianskii.pits.communicator.app.grpc;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceGrpc;
import io.grpc.*;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@Component
public class GrpcServer {
    private final GrpcProperties grpcProperties;
    private final RabbitTemplate rabbitTemplate;

    private Server server;

    public GrpcServer(GrpcProperties grpcProperties,
                      RabbitTemplate rabbitTemplate) {
        this.grpcProperties = requireNonNull(grpcProperties);
        this.rabbitTemplate = requireNonNull(rabbitTemplate);
    }

    @EventListener
    public void handleContextStart(ContextRefreshedEvent event) throws IOException {
        server = ServerBuilder.forPort(grpcProperties.getPort())
                .intercept(new RequestLoggingInterceptor())
                .addService(new EquipmentServiceImpl(rabbitTemplate))
                .build();

        server.start();
    }

    @EventListener
    public void handleContextStop(ContextStoppedEvent event) throws InterruptedException {
        server.shutdown();
        server.awaitTermination();
    }

    private static class RequestLoggingInterceptor implements ServerInterceptor {
        private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

        @Override
        public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
            final var listener = next.startCall(call, headers);
            return new SimpleForwardingServerCallListener<>(listener) {
                @Override
                public void onMessage(ReqT message) {
                    LOG.info("Call: " + System.lineSeparator() + message);
                    super.onMessage(message);
                }
            };
        }
    }
}
