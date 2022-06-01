package com.github.vkremianskii.pits.registry.json;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.vkremianskii.pits.registry.json.deserializer.EquipmentDeserializer;
import com.github.vkremianskii.pits.registry.json.deserializer.LocationDeserializer;
import com.github.vkremianskii.pits.registry.model.Equipment;
import com.github.vkremianskii.pits.registry.model.Location;

import java.util.Map;

public class RegistryModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new SimpleDeserializers(Map.of(
            Equipment.class, new EquipmentDeserializer(),
            Location.class, new LocationDeserializer()
        )));
    }
}
