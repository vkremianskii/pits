package com.github.vkremianskii.pits.core.types.json;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.core.types.model.EquipmentType.TRUCK;

public class CoreTypesModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new SimpleSerializers(List.of(
            new MicrotypeSerializer(),
            new EquipmentStateSerializer()
        )));
        context.addDeserializers(new SimpleDeserializers(Map.of(
            EquipmentId.class, new MicrotypeDeserializer<>(EquipmentId.class, UUID.class),
            Equipment.class, new EquipmentDeserializer(),
            DozerState.class, new EquipmentStateDeserializer(DOZER),
            DrillState.class, new EquipmentStateDeserializer(DRILL),
            ShovelState.class, new EquipmentStateDeserializer(SHOVEL),
            TruckState.class, new EquipmentStateDeserializer(TRUCK),
            LocationId.class, new MicrotypeDeserializer<>(LocationId.class, UUID.class),
            Location.class, new LocationDeserializer()
        )));
    }
}
