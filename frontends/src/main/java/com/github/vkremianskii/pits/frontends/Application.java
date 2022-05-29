package com.github.vkremianskii.pits.frontends;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.vkremianskii.pits.core.types.json.CoreTypesModule;
import com.github.vkremianskii.pits.frontends.grpc.GrpcClient;
import com.github.vkremianskii.pits.frontends.logic.MainViewPresenterImpl;
import com.github.vkremianskii.pits.frontends.ui.MainViewImpl;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.infra.RegistryCodecConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class Application {

    public static void main(String[] args) {
        final var objectMapper = new Jackson2ObjectMapperBuilder()
            .modules(new CoreTypesModule())
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();
        final var codecConfigurer = new RegistryCodecConfigurer(objectMapper);
        final var registryClient = new RegistryClient("http://localhost:8080", codecConfigurer);

        final var grpcClient = new GrpcClient();
        grpcClient.start();

        final var mainViewPresenter = new MainViewPresenterImpl(registryClient, grpcClient);
        final var mainView = new MainViewImpl(mainViewPresenter);
        mainView.initialize();

        mainViewPresenter.setView(mainView);
        mainViewPresenter.start();
    }
}
