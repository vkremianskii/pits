package com.github.vkremianskii.pits.registry.api;

import com.github.vkremianskii.pits.auth.client.AuthClient;
import com.github.vkremianskii.pits.auth.dto.AuthenticateResponse;
import com.github.vkremianskii.pits.core.model.LatLngPoint;
import com.github.vkremianskii.pits.core.model.LocationPoint;
import com.github.vkremianskii.pits.core.web.CoreWebAutoConfiguration;
import com.github.vkremianskii.pits.registry.data.LocationPointRepository;
import com.github.vkremianskii.pits.registry.data.LocationRepository;
import com.github.vkremianskii.pits.registry.dto.CreateLocationRequest;
import com.github.vkremianskii.pits.registry.logic.LocationService;
import com.github.vkremianskii.pits.registry.model.LocationDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static com.github.vkremianskii.pits.auth.model.Scope.scope;
import static com.github.vkremianskii.pits.auth.model.Username.username;
import static com.github.vkremianskii.pits.core.TestLocations.randomLocationId;
import static com.github.vkremianskii.pits.core.model.LocationId.locationId;
import static com.github.vkremianskii.pits.core.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.core.model.LocationType.FACE;
import static com.github.vkremianskii.pits.core.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.core.model.LocationType.STOCKPILE;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = LocationController.class)
@Import(CoreWebAutoConfiguration.class)
class LocationControllerTests {

    @MockBean
    AuthClient authClient;
    @MockBean
    LocationService locationService;
    @MockBean
    LocationRepository locationRepository;
    @MockBean
    LocationPointRepository locationPointRepository;
    @Autowired
    WebTestClient webClient;

    @BeforeEach
    void setup() {
        when(authClient.authenticateUser(username("admin"), "admin".toCharArray()))
            .thenReturn(Mono.just(new AuthenticateResponse(Set.of(scope("scope")))));
    }

    @Test
    void should_get_locations() {
        // given
        var dumpId = randomLocationId();
        var faceId = randomLocationId();
        var holeId = randomLocationId();
        var stockpileId = randomLocationId();
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
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
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
        when(locationService.createLocation(any(), any(), any()))
            .thenReturn(Mono.defer(() -> Mono.just(locationId(randomUUID()))));

        // expect
        webClient.post()
            .uri("/location")
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
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
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateLocationRequest("Face No.1", FACE, emptyList()))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.locationId").isNotEmpty();
        webClient.post()
            .uri("/location")
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateLocationRequest("Hole No.1", HOLE, emptyList()))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.locationId").isNotEmpty();
        webClient.post()
            .uri("/location")
            .header(AUTHORIZATION, "Basic YWRtaW46YWRtaW4=")
            .contentType(APPLICATION_JSON)
            .bodyValue(new CreateLocationRequest("Stockpile No.1", STOCKPILE, emptyList()))
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.locationId").isNotEmpty();
    }
}
