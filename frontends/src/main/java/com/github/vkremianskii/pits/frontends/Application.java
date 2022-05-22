package com.github.vkremianskii.pits.frontends;

import com.github.vkremianskii.pits.frontends.grpc.GrpcClient;
import com.github.vkremianskii.pits.frontends.ui.MainView;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.json.RegistryCodecConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class Application {

    public static void main(String[] args) {
        final var codecConfigurer = new RegistryCodecConfigurer(new Jackson2ObjectMapperBuilder());
        final var registryClient = new RegistryClient("http://localhost:8080", codecConfigurer);

        final var grpcClient = new GrpcClient();
        grpcClient.start();

        final var mainView = new MainView(registryClient, grpcClient);
        mainView.initialize();
    }
}
