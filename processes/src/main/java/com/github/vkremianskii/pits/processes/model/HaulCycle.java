package com.github.vkremianskii.pits.processes.model;

import org.jetbrains.annotations.Nullable;

import java.time.Instant;

public record HaulCycle(long id,
                        int truckId,
                        Instant insertTimestamp,
                        @Nullable Integer shovelId,
                        @Nullable Instant waitLoadTimestamp,
                        @Nullable Instant startLoadTimestamp,
                        @Nullable Double startLoadLatitude,
                        @Nullable Double startLoadLongitude,
                        @Nullable Instant endLoadTimestamp,
                        @Nullable Integer endLoadPayload,
                        @Nullable Instant startUnloadTimestamp,
                        @Nullable Instant endUnloadTimestamp) {
}
