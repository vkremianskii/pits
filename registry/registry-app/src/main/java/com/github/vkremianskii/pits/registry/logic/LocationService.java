package com.github.vkremianskii.pits.registry.logic;

import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.core.model.LocationId;
import com.github.vkremianskii.pits.core.model.LocationType;
import com.github.vkremianskii.pits.registry.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.data.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.vkremianskii.pits.core.Pair.pair;
import static com.github.vkremianskii.pits.core.model.LocationId.locationId;
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
            .mapToObj(i -> pair(i, geometry.get(i)))
            .toList();
        return locationRepository.createLocation(locationId, name, type)
            .then(Flux.fromIterable(indexedPoints)
                .flatMap(pair -> locationPointRepository.createLocationPoint(
                    locationId,
                    pair.left(),
                    pair.right().latitude(),
                    pair.right().longitude()))
                .then())
            .thenReturn(locationId);
    }
}
