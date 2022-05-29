package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.core.types.model.LatLngPoint;
import com.github.vkremianskii.pits.core.types.model.Location;
import com.github.vkremianskii.pits.core.types.model.LocationPoint;
import com.github.vkremianskii.pits.core.types.model.location.Dump;
import com.github.vkremianskii.pits.core.types.model.location.Face;
import com.github.vkremianskii.pits.core.types.model.location.Hole;
import com.github.vkremianskii.pits.core.types.model.location.Stockpile;
import com.github.vkremianskii.pits.registry.app.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import com.github.vkremianskii.pits.registry.app.logic.LocationService;
import com.github.vkremianskii.pits.registry.types.dto.CreateLocationRequest;
import com.github.vkremianskii.pits.registry.types.dto.CreateLocationResponse;
import com.github.vkremianskii.pits.registry.types.dto.LocationsResponse;
import com.github.vkremianskii.pits.registry.types.model.LocationDeclaration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    private final LocationService locationService;
    private final LocationRepository locationRepository;
    private final LocationPointRepository locationPointRepository;

    public LocationController(LocationService locationService,
                              LocationRepository locationRepository,
                              LocationPointRepository locationPointRepository) {
        this.locationService = requireNonNull(locationService);
        this.locationRepository = requireNonNull(locationRepository);
        this.locationPointRepository = requireNonNull(locationPointRepository);
    }

    @GetMapping
    public Mono<LocationsResponse> getLocations() {
        return locationRepository.getLocations()
            .flatMap(locations -> Flux.fromIterable(locations)
                .flatMap(location -> locationPointRepository.getPointsByLocationId(location.id())
                    .switchIfEmpty(Mono.just(emptyList()))
                    .map(points -> pair(location, points)))
                .collectList()
                .map(pairs -> new LocationsResponse(pairs.stream()
                    .map(pair -> location(pair.left(), pair.right()))
                    .toList())));
    }

    @PostMapping
    public Mono<CreateLocationResponse> createLocation(@RequestBody CreateLocationRequest request) {
        return locationService.createLocation(request.name(), request.type(), request.geometry())
            .map(CreateLocationResponse::new);
    }

    private static Location location(LocationDeclaration declaration, List<LocationPoint> points) {
        final var geometry = points.stream()
            .map(point -> new LatLngPoint(point.latitude(), point.longitude()))
            .toList();
        return switch (declaration.type()) {
            case DUMP -> new Dump(declaration.id(), declaration.name(), geometry);
            case FACE -> new Face(declaration.id(), declaration.name(), geometry);
            case HOLE -> new Hole(declaration.id(), declaration.name(), geometry);
            case STOCKPILE -> new Stockpile(declaration.id(), declaration.name(), geometry);
        };
    }
}
