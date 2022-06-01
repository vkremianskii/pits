package com.github.vkremianskii.pits.frontends;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.vkremianskii.pits.core.json.CoreModule;
import com.github.vkremianskii.pits.frontends.grpc.GrpcClient;
import com.github.vkremianskii.pits.frontends.logic.MainViewPresenterImpl;
import com.github.vkremianskii.pits.frontends.ui.MainViewImpl;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.client.RegistryProperties;
import com.github.vkremianskii.pits.registry.json.RegistryModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class Application {

    public static void main(String[] args) {
        final var registryProperties = new RegistryProperties("http://localhost:8081");
        final var objectMapper = new Jackson2ObjectMapperBuilder()
            .modules(new CoreModule(), new RegistryModule())
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();
        final var registryClient = new RegistryClient(registryProperties, objectMapper);

        final var grpcClient = new GrpcClient();
        grpcClient.start();

        final var mainViewPresenter = new MainViewPresenterImpl(registryClient, grpcClient);
        final var mainView = new MainViewImpl(mainViewPresenter);
        mainView.initialize();

        mainViewPresenter.setView(mainView);
        mainViewPresenter.start();
    }
}
