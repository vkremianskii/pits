package com.github.vkremianskii.pits.registry.app.logic;

import com.github.vkremianskii.pits.core.types.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.app.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.vkremianskii.pits.core.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.core.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.core.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.core.types.model.LocationType.STOCKPILE;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocationServiceTests {

    LocationRepository locationRepository = mock(LocationRepository.class);
    LocationPointRepository locationPointRepository = mock(LocationPointRepository.class);
    LocationService sut = new LocationService(
        locationRepository,
        locationPointRepository);

    @Test
    void should_create_locations() {
        // given
        when(locationRepository.createLocation(any(), any(), any()))
            .thenReturn(Mono.empty());
        when(locationPointRepository.createLocationPoint(any(), anyInt(), anyDouble(), anyDouble()))
            .thenReturn(Mono.empty());

        // when
        sut.createLocation("Dump No.1", DUMP, List.of(
            new LatLngPoint(41.1494512, -8.6107884),
            new LatLngPoint(41.1494512, -8.6107884)
        )).block();
        sut.createLocation("Face No.1", FACE, emptyList()).block();
        sut.createLocation("Hole No.1", HOLE, emptyList()).block();
        sut.createLocation("Stockpile No.1", STOCKPILE, emptyList()).block();

        // then
        verify(locationRepository, times(4)).createLocation(any(), any(), any());
        verify(locationPointRepository, times(2)).createLocationPoint(any(), anyInt(), anyDouble(), anyDouble());
    }
}
