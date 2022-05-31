package com.github.vkremianskii.pits.core;

import com.github.vkremianskii.pits.core.model.EquipmentId;
import com.github.vkremianskii.pits.core.model.EquipmentState;
import com.github.vkremianskii.pits.core.model.Position;
import com.github.vkremianskii.pits.core.model.equipment.Dozer;
import com.github.vkremianskii.pits.core.model.equipment.Drill;
import com.github.vkremianskii.pits.core.model.equipment.Shovel;
import com.github.vkremianskii.pits.core.model.equipment.Truck;
import org.jetbrains.annotations.Nullable;

import static java.util.UUID.randomUUID;

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
        return EquipmentId.equipmentId(randomUUID());
    }

    private TestEquipment() {
    }
}
