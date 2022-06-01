package com.github.vkremianskii.pits.registry.logic;

import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.data.LocationRepository;
import com.github.vkremianskii.pits.registry.model.LocationId;
import com.github.vkremianskii.pits.registry.model.LocationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.vkremianskii.pits.core.Tuple2.tuple;
import static com.github.vkremianskii.pits.registry.model.LocationId.locationId;
import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationPointRepository locationPointRepository;

    public LocationService(LocationRepository locationRepository,
                           LocationPointRepository locationPointRepository) {
        this.locationRepository = requireNonNull(locationRepository);
        this.locationPointRepository = requireNonNull(locationPointRepository);
    }

    @Transactional
    public Mono<LocationId> createLocation(String name, LocationType type, List<LatLngPoint> geometry) {
        final var locationId = locationId(randomUUID());
        final var indexedPoints = IntStream.range(0, geometry.size())
            .mapToObj(i -> tuple(i, geometry.get(i)))
            .toList();
        return locationRepository.createLocation(locationId, name, type)
            .then(Flux.fromIterable(indexedPoints)
                .flatMap(pair -> locationPointRepository.createLocationPoint(
                    locationId,
                    pair.first(),
                    pair.second().latitude(),
                    pair.second().longitude()))
                .then())
            .thenReturn(locationId);
    }
}
