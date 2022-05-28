package com.github.vkremianskii.pits.frontends.logic;

import com.github.vkremianskii.pits.frontends.grpc.GrpcClient;
import com.github.vkremianskii.pits.frontends.ui.MainView;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.dto.EquipmentResponse;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.TreeMap;
import java.util.stream.Stream;

import static com.github.vkremianskii.pits.core.types.Pair.pair;
import static com.github.vkremianskii.pits.core.types.Tuple3.tuple;
import static com.github.vkremianskii.pits.frontends.ui.ViewUtil.uiThread;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DOZER;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.DRILL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.SHOVEL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class MainViewPresenterImpl implements MainViewPresenter {

    private static final Logger LOG = LoggerFactory.getLogger(MainViewPresenterImpl.class);
    private static final int DEFAULT_ELEVATION = 0;

    private final RegistryClient registryClient;
    private final GrpcClient grpcClient;
    private final TreeMap<Integer, Equipment> equipmentById = new TreeMap<>();

    private MainView view;
    private Disposable fleetRefreshment;

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
    }

    private Mono<Void> refreshFleet() {
        return registryClient.getEquipment()
            .doOnSuccess(response -> {
                uiThread(() -> view.setInitializeFleetEnabled(response.equipment().isEmpty()));
                final var newEquipmentById = response.equipment().stream().collect(toMap(e -> e.id, identity()));
                if (newEquipmentById.equals(equipmentById)) {
                    return;
                }
                equipmentById.clear();
                equipmentById.putAll(newEquipmentById);
                uiThread(() -> view.refreshFleetControls(equipmentById));
            })
            .onErrorResume(e -> {
                LOG.error("Error while fetching equipment from registry", e);
                return Mono.just(new EquipmentResponse(emptyList()));
            })
            .then();
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
    public void sendEquipmentPosition(int equipmentId, double latitude, double longitude, int elevation) {
        grpcClient.sendPositionChanged(equipmentId, latitude, longitude, elevation);
    }

    @Override
    public void sendEquipmentPayload(int equipmentId, int payload) {
        grpcClient.sendPayloadChanged(equipmentId, payload);
    }

    @Override
    public void onWindowClosing() {
        try {
            if (fleetRefreshment != null) {
                fleetRefreshment.dispose();
            }
            grpcClient.shutdown();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void onEquipmentSelected(int equipmentId) {
        final var equipment = equipmentById.get(equipmentId);
        view.refreshEquipmentControls(equipment);
    }
}
