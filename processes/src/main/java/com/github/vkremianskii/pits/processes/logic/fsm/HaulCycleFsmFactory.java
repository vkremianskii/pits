package com.github.vkremianskii.pits.processes.logic.fsm;

import com.github.vkremianskii.pits.processes.model.EquipmentPositionRecord;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;

import java.time.Instant;
import java.util.Map;
import java.util.SortedMap;

public class HaulCycleFsmFactory {

    public HaulCycleFsm create(Map<Shovel, SortedMap<Instant, EquipmentPositionRecord>> shovelToOrderedPositions,
                               HaulCycleFsmSink haulCycleSink) {
        return new HaulCycleFsm(
            shovelToOrderedPositions,
            haulCycleSink);
    }
}
