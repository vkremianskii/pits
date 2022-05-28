package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.registry.app.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import com.github.vkremianskii.pits.registry.types.dto.LocationsResponse;
import com.github.vkremianskii.pits.registry.types.model.Location;
import com.github.vkremianskii.pits.registry.types.model.LocationPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.vkremianskii.pits.core.types.Pair.pair;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/location")
public class LocationController {

    private final LocationRepository locationRepository;
    private final LocationPointRepository locationPointRepository;

    public LocationController(LocationRepository locationRepository,
                              LocationPointRepository locationPointRepository) {
        this.locationRepository = requireNonNull(locationRepository);
        this.locationPointRepository = requireNonNull(locationPointRepository);
    }

    @GetMapping
    public Mono<LocationsResponse> getLocations() {
        return locationRepository.getLocations()
            .flatMap(locations -> Flux.fromIterable(locations)
                .flatMap(location -> locationPointRepository.getPointsByLocationId(location.id)
                    .switchIfEmpty(Mono.just(emptyList()))
                    .map(points -> pair(location, points)))
                .collectList()
                .map(pairs -> new LocationsResponse(pairs.stream()
                    .map(pair -> responseLocation(pair.left(), pair.right()))
                    .toList())));
    }

    private static LocationsResponse.Location responseLocation(Location location, List<LocationPoint> points) {
        return new LocationsResponse.Location(
            location.id,
            location.name,
            location.type,
            points.stream().map(LocationController::responseLocationPoint).toList());
    }

    private static LocationsResponse.LocationPoint responseLocationPoint(LocationPoint point) {
        return new LocationsResponse.LocationPoint(point.latitude(), point.longitude());
    }
}
