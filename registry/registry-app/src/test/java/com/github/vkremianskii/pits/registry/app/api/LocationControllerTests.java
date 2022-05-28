package com.github.vkremianskii.pits.registry.app.api;

import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import com.github.vkremianskii.pits.registry.app.data.EquipmentRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import com.github.vkremianskii.pits.registry.types.dto.CreateLocationRequest;
import com.github.vkremianskii.pits.registry.types.model.LatLngPoint;
import com.github.vkremianskii.pits.registry.types.model.LocationDeclaration;
import com.github.vkremianskii.pits.registry.types.model.LocationPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.STOCKPILE;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest
@Import(CoreWebAutoConfiguration.class)
class LocationControllerTests {

    @MockBean
    EquipmentRepository equipmentRepository;
    @MockBean
    LocationRepository locationRepository;
    @MockBean
    LocationPointRepository locationPointRepository;
    @MockBean
    PlatformTransactionManager transactionManager;
    @Autowired
    WebTestClient webClient;

    @Test
    void should_get_locations() {
        // given
        var dumpId = UUID.randomUUID();
        var faceId = UUID.randomUUID();
        var holeId = UUID.randomUUID();
        var stockpileId = UUID.randomUUID();
        when(locationRepository.getLocations())
            .thenReturn(Mono.just(List.of(
                new LocationDeclaration(dumpId, "Dump No.1", DUMP),
                new LocationDeclaration(faceId, "Face No.1", FACE),
                new LocationDeclaration(holeId, "Hole No.1", HOLE),
                new LocationDeclaration(stockpileId, "Stockpile No.1", STOCKPILE))));
        when(locationPointRepository.getPointsByLocationId(dumpId))
            .thenReturn(Mono.just(List.of(new LocationPoint(1, dumpId, 0, 41.1494512, -8.6107884))));
        when(locationPointRepository.getPointsByLocationId(faceId))
            .thenReturn(Mono.empty());
        when(locationPointRepository.getPointsByLocationId(holeId))
            .thenReturn(Mono.empty());
        when(locationPointRepository.getPointsByLocationId(stockpileId))
            .thenReturn(Mono.empty());

        // expect
        webClient.get()
            .uri("/location")
            .exchange()
            .expectStatus().isOk()
            .expectBody().json("""
                {
                    "locations": [{
                        "name": "Dump No.1",
                        "type": "DUMP",
                        "geometry": [{
                            "latitude": 41.1494512,
                            "longitude": -8.6107884
                        }]
                    },{
                        "name": "Face No.1",
                        "type": "FACE",
                        "geometry": []
                    },{
                        "name": "Hole No.1",
                        "type": "HOLE",
                        "geometry": []
                    },{
                        "name": "Stockpile No.1",
                        "type": "STOCKPILE",
                        "geometry": []
                    }]
                }
                """);
    }

    @Test
    void should_create_locations() {
        // given
        when(locationRepository.createLocation(any(), any(), same(DUMP)))
            .thenReturn(Mono.empty());
        when(locationPointRepository.createLocationPoint(any(), eq(0), eq(41.1494512), eq(-8.6107884)))
            .thenReturn(Mono.empty());
        when(locationPointRepository.createLocationPoint(any(), eq(1), eq(41.1494512), eq(-8.6107884)))
            .thenReturn(Mono.empty());
        when(locationRepository.createLocation(any(), any(), same(FACE)))
            .thenReturn(Mono.empty());
        when(locationRepository.createLocation(any(), any(), same(HOLE)))
            .thenReturn(Mono.empty());
        when(locationRepository.createLocation(any(), any(), same(STOCKPILE)))
            .thenReturn(Mono.empty());

        // expect
        webClient.post()
            .uri("/location")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateLocationRequest("Dump No.1", DUMP, List.of(
                new LatLngPoint(41.1494512, -8.6107884),
                new LatLngPoint(41.1494512, -8.6107884)
            )))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.locationId").isNotEmpty();
        webClient.post()
            .uri("/location")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateLocationRequest("Face No.1", FACE, emptyList()))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.locationId").isNotEmpty();
        webClient.post()
            .uri("/location")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateLocationRequest("Hole No.1", HOLE, emptyList()))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.locationId").isNotEmpty();
        webClient.post()
            .uri("/location")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateLocationRequest("Stockpile No.1", STOCKPILE, emptyList()))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.locationId").isNotEmpty();
        verify(transactionManager, times(4)).commit(any());
    }
}
