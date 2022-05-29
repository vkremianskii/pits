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
import com.github.vkremianskii.pits.registry.types.dto.CreateLocationRequest;
import com.github.vkremianskii.pits.registry.types.dto.CreateLocationResponse;
import com.github.vkremianskii.pits.registry.types.dto.LocationsResponse;
import com.github.vkremianskii.pits.registry.types.model.LocationDeclaration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.github.vkremianskii.pits.core.types.Pair.pair;
import static com.github.vkremianskii.pits.core.types.model.LocationId.locationId;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

@RestController
@RequestMapping("/location")
public class LocationController {

    private final LocationRepository locationRepository;
    private final LocationPointRepository locationPointRepository;
    private final PlatformTransactionManager transactionManager;

    public LocationController(LocationRepository locationRepository,
                              LocationPointRepository locationPointRepository,
                              PlatformTransactionManager transactionManager) {
        this.locationRepository = requireNonNull(locationRepository);
        this.locationPointRepository = requireNonNull(locationPointRepository);
        this.transactionManager = requireNonNull(transactionManager);
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
        final var locationId = locationId(UUID.randomUUID());
        final var indexedPoints = IntStream.range(0, request.geometry().size())
            .mapToObj(i -> pair(i, request.geometry().get(i)))
            .toList();
        final var txDefinition = new DefaultTransactionDefinition(PROPAGATION_REQUIRES_NEW);
        final var txStatus = transactionManager.getTransaction(txDefinition);

        return locationRepository.createLocation(locationId, request.name(), request.type())
            .then(Flux.fromIterable(indexedPoints)
                .flatMap(pair -> locationPointRepository.createLocationPoint(
                    locationId,
                    pair.left(),
                    pair.right().latitude(),
                    pair.right().longitude()))
                .then())
            .thenReturn(new CreateLocationResponse(locationId))
            .doOnSuccess(__ -> transactionManager.commit(txStatus))
            .doOnError(__ -> transactionManager.rollback(txStatus));
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
