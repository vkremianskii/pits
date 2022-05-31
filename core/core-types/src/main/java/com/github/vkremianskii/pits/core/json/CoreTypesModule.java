package com.github.vkremianskii.pits.core.json;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.github.vkremianskii.pits.core.json.deserializer.EquipmentDeserializer;
import com.github.vkremianskii.pits.core.json.serializer.MicrotypeSerializer;
import com.github.vkremianskii.pits.core.json.deserializer.LocationDeserializer;
import com.github.vkremianskii.pits.core.model.Equipment;
import com.github.vkremianskii.pits.core.model.Location;

import java.util.List;
import java.util.Map;

public class CoreTypesModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new SimpleSerializers(List.of(
            new MicrotypeSerializer()
        )));
        context.addDeserializers(new SimpleDeserializers(Map.of(
            Equipment.class, new EquipmentDeserializer(),
            Location.class, new LocationDeserializer()
        )));
    }
}