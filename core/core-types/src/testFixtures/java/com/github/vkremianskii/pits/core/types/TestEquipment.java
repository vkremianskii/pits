package com.github.vkremianskii.pits.core.types;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.core.types.model.EquipmentState;
import com.github.vkremianskii.pits.core.types.model.Position;
import com.github.vkremianskii.pits.core.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.core.types.model.equipment.Drill;
import com.github.vkremianskii.pits.core.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.core.types.model.equipment.Truck;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.github.vkremianskii.pits.core.types.model.EquipmentId.equipmentId;

public class TestEquipment {

    public static Dozer aDozer() {
        return new Dozer(randomEquipmentId(), "Some dozer", null, null);
    }

    public static Drill aDrill() {
        return new Drill(randomEquipmentId(), "Some drill", null, null);
    }

    public static Shovel aShovel() {
        return new Shovel(randomEquipmentId(), "Some shovel", 20, null, null);
    }

    public static Truck aTruck() {
        return new Truck(randomEquipmentId(), "Some truck", null, null, null);
    }

    public static Truck aTruck(@Nullable EquipmentState state, @Nullable Position position, @Nullable Integer payload) {
        return new Truck(randomEquipmentId(), "Some truck", state, position, payload);
    }

    public static EquipmentId randomEquipmentId() {
        return equipmentId(UUID.randomUUID());
    }

    private TestEquipment() {
    }
}
