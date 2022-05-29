package com.github.vkremianskii.pits.frontends.logic;

import com.github.vkremianskii.pits.core.types.model.EquipmentId;
import com.github.vkremianskii.pits.frontends.grpc.GrpcClient;
import com.github.vkremianskii.pits.frontends.ui.MainView;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentResponse;
import com.github.vkremianskii.pits.registry.types.dto.LocationsResponse;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.types.model.Location;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

import static com.github.vkremianskii.pits.core.types.Pair.pair;
import static com.github.vkremianskii.pits.core.types.Tuple3.tuple;
import static com.github.vkremianskii.pits.frontends.ui.ViewUtil.uiThread;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.STOCKPILE;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class MainViewPresenterImpl implements MainViewPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(MainViewPresenterImpl.class);
    private static final int DEFAULT_ELEVATION = 0;

    private final RegistryClient registryClient;
    private final GrpcClient grpcClient;
    private final TreeMap<EquipmentId, Equipment> equipmentById = new TreeMap<>();
    private final List<Location> locations = new ArrayList<>();

    private MainView view;
    private Disposable fleetRefreshment;
    private Disposable locationsRefreshment;

    public MainViewPresenterImpl(RegistryClient registryClient, GrpcClient grpcClient) {
        this.registryClient = requireNonNull(registryClient);
        this.grpcClient = requireNonNull(grpcClient);
    }

    public void setView(MainView view) {
        this.view = requireNonNull(view);
    }

    public void start() {
        fleetRefreshment = Flux.interval(Duration.ofSeconds(1))
            .flatMap(__ -> refreshFleet())
            .subscribe();

        locationsRefreshment = Flux.interval(Duration.ofSeconds(5))
            .flatMap(__ -> refreshLocations())
            .subscribe();
    }

    private Mono<Void> refreshFleet() {
        return registryClient.getEquipment()
            .doOnSuccess(response -> {
                uiThread(() -> view.setInitializeFleetEnabled(response.equipment().isEmpty()));
                final var newEquipmentById = new TreeMap<>(response.equipment().stream()
                    .collect(toMap(e -> e.id, identity())));
                if (newEquipmentById.equals(equipmentById)) {
                    return;
                }
                equipmentById.clear();
                equipmentById.putAll(newEquipmentById);
                uiThread(() -> view.refreshFleet(equipmentById));
            })
            .onErrorResume(e -> {
                LOG.error("Error while fetching equipment from registry", e);
                return Mono.just(new EquipmentResponse(emptyList()));
            })
            .then();
    }

    private Mono<Void> refreshLocations() {
        return registryClient.getLocations()
            .doOnSuccess(response -> {
                uiThread(() -> view.setInitializeLocationsEnabled(response.locations().isEmpty()));
                if (locations.equals(response.locations())) {
                    return;
                }
                locations.clear();
                locations.addAll(response.locations());
                uiThread(() -> view.refreshLocations(locations));
            })
            .onErrorResume(e -> {
                LOG.error("Error while fetching locations from registry", e);
                return Mono.just(new LocationsResponse(emptyList()));
            })
            .then();
    }

    @Override
    public void sendEquipmentPosition(EquipmentId equipmentId, double latitude, double longitude, int elevation) {
        grpcClient.sendPositionChanged(equipmentId, latitude, longitude, elevation);
    }

    @Override
    public void sendEquipmentPayload(EquipmentId equipmentId, int payload) {
        grpcClient.sendPayloadChanged(equipmentId, payload);
    }

    @Override
    public void initializeFleet() {
        Flux.fromStream(Stream.of(
                tuple("Dozer No.1", DOZER, new Position(65.305376, 41.026554, DEFAULT_ELEVATION)),
                tuple("Drill No.1", DRILL, new Position(65.299853, 41.019001, DEFAULT_ELEVATION)),
                tuple("Shovel No.1", SHOVEL, new Position(65.303583, 41.019173, DEFAULT_ELEVATION)),
                tuple("Truck No.1", TRUCK, new Position(65.294329, 41.026382, DEFAULT_ELEVATION))))
            .flatMap(tuple -> registryClient.createEquipment(tuple.first(), tuple.second())
                .map(response -> pair(response.equipmentId(), tuple.third())))
            .flatMap(pair -> grpcClient.sendPositionChanged(
                pair.left(),
                pair.right().latitude(),
                pair.right().longitude(),
                pair.right().elevation()))
            .onErrorResume(e -> {
                LOG.error("Error while initializing fleet", e);
                return Mono.empty();
            })
            .then()
            .block();
    }

    @Override
    public void initializeLocations() {
        Flux.fromStream(Stream.of(
                tuple("Dump No.1", DUMP, List.of(
                    new LatLngPoint(65.299351, 41.042862),
                    new LatLngPoint(65.312403, 41.053848),
                    new LatLngPoint(65.309391, 41.074791),
                    new LatLngPoint(65.297342, 41.052475)
                )),
                tuple("Face No.1", FACE, List.<LatLngPoint>of()),
                tuple("Hole No.1", HOLE, List.<LatLngPoint>of()),
                tuple("Stockpile No.1", STOCKPILE, List.<LatLngPoint>of())))
            .flatMap(tuple -> registryClient.createLocation(tuple.first(), tuple.second(), tuple.third()))
            .onErrorResume(e -> {
                LOG.error("Error while initializing localisations", e);
                return Mono.empty();
            })
            .then()
            .block();
    }

    @Override
    public void onWindowClosing() {
        try {
            if (fleetRefreshment != null) {
                fleetRefreshment.dispose();
            }
            if (locationsRefreshment != null) {
                locationsRefreshment.dispose();
            }
            grpcClient.shutdown();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void onEquipmentSelected(EquipmentId equipmentId) {
        final var equipment = equipmentById.get(equipmentId);
        view.refreshEquipment(equipment);
    }
}
