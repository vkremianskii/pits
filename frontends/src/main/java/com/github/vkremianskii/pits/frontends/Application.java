package com.github.vkremianskii.pits.frontends;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vkremianskii.pits.core.types.json.deserializer.EquipmentDeserializer;
import com.github.vkremianskii.pits.core.types.json.deserializer.EquipmentStateDeserializer;
import com.github.vkremianskii.pits.core.types.json.deserializer.LocationDeserializer;
import com.github.vkremianskii.pits.core.types.json.deserializer.MicrotypeDeserializer;
import com.github.vkremianskii.pits.core.types.json.serializer.EquipmentStateSerializer;
import com.github.vkremianskii.pits.core.types.json.serializer.MicrotypeSerializer;
import com.github.vkremianskii.pits.core.types.model.Equipment;
import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.Location;
import com.github.vkremianskii.pits.core.types.model.LocationId;
import com.github.vkremianskii.pits.core.types.model.equipment.DozerState;
import com.github.vkremianskii.pits.core.types.model.equipment.DrillState;
import com.github.vkremianskii.pits.core.types.model.equipment.ShovelState;
import com.github.vkremianskii.pits.core.types.model.equipment.TruckState;
import com.github.vkremianskii.pits.frontends.grpc.GrpcClient;
import com.github.vkremianskii.pits.frontends.logic.MainViewPresenterImpl;
import com.github.vkremianskii.pits.frontends.ui.MainViewImpl;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.infra.RegistryCodecConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.TRUCK;

public class Application {

    public static void main(String[] args) {
        final var objectMapper = new Jackson2ObjectMapperBuilder()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .serializerByType(EquipmentId.class, new MicrotypeSerializer())
            .serializerByType(LocationId.class, new MicrotypeSerializer())
            .serializerByType(DozerState.class, new EquipmentStateSerializer())
            .serializerByType(DrillState.class, new EquipmentStateSerializer())
            .serializerByType(ShovelState.class, new EquipmentStateSerializer())
            .serializerByType(TruckState.class, new EquipmentStateSerializer())
            .deserializerByType(EquipmentId.class, new MicrotypeDeserializer<>(EquipmentId.class, UUID.class))
            .deserializerByType(Equipment.class, new EquipmentDeserializer())
            .deserializerByType(DozerState.class, new EquipmentStateDeserializer(DOZER))
            .deserializerByType(DrillState.class, new EquipmentStateDeserializer(DRILL))
            .deserializerByType(ShovelState.class, new EquipmentStateDeserializer(SHOVEL))
            .deserializerByType(TruckState.class, new EquipmentStateDeserializer(TRUCK))
            .deserializerByType(LocationId.class, new MicrotypeDeserializer<>(LocationId.class, UUID.class))
            .deserializerByType(Location.class, new LocationDeserializer())
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
