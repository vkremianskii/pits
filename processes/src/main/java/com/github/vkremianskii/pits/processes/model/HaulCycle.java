package com.github.vkremianskii.pits.processes.model;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public record HaulCycle(long id,
                        EquipmentId truckId,
                        Instant insertTimestamp,
                        @Nullable EquipmentId shovelId,
                        @Nullable Instant waitLoadTimestamp,
                        @Nullable Instant startLoadTimestamp,
                        @Nullable Double startLoadLatitude,
                        @Nullable Double startLoadLongitude,
                        @Nullable Instant endLoadTimestamp,
                        @Nullable Integer endLoadPayload,
                        @Nullable Instant startUnloadTimestamp,
                        @Nullable Instant endUnloadTimestamp) {

}
