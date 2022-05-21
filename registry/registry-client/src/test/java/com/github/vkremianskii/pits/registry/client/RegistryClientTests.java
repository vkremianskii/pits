package com.github.vkremianskii.pits.registry.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.vkremianskii.pits.registry.types.json.RegistryCodecConfigurer;
import com.github.vkremianskii.pits.registry.types.model.equipment.Dozer;
import com.github.vkremianskii.pits.registry.types.model.equipment.Drill;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.location.Dump;
import com.github.vkremianskii.pits.registry.types.model.location.Face;
import com.github.vkremianskii.pits.registry.types.model.location.Hole;
import com.github.vkremianskii.pits.registry.types.model.location.Stockpile;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@WireMockTest
class RegistryClientTests {
    Jackson2ObjectMapperBuilder objectMapperBuilder = new Jackson2ObjectMapperBuilder();

    @Test
    void should_get_equipment(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(get(urlPathEqualTo("/equipment")).willReturn(aResponse()
                .withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                .withBody("""       
                        [{
                            "id": 1,
                            "name": "Dozer No.1",
                            "type": "DOZER"
                        },{
                            "id": 2,
                            "name": "Drill No.1",
                            "type": "DRILL"
                        },{
                            "id": 3,
                            "name": "Shovel No.1",
                            "type": "SHOVEL"
                        },{
                            "id": 4,
                            "name": "Truck No.1",
                            "type": "TRUCK",
                            "state": "HAUL",
                            "position": {
                                "latitude": 41.1494512,
                                "longitude": -8.6107884,
                                "elevation": 86
                            },
                            "payloadWeight": 10
                        }]
                        """)));
        var webClient = newWebClient(wmRuntimeInfo);
        var sut = new RegistryClient(webClient);

        // when
        var equipment = sut.getEquipment().block();

        // then
        assertThat(equipment).hasSize(4);
        assertThat(equipment).hasAtLeastOneElementOfType(Dozer.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Drill.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Shovel.class);
        assertThat(equipment).hasAtLeastOneElementOfType(Truck.class);
        verify(getRequestedFor(urlPathEqualTo("/equipment")));
    }

    @Test
    void should_get_locations(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(get(urlPathEqualTo("/locations")).willReturn(aResponse()
                .withStatus(200)
                .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
                .withBody("""        
                        [{
                            "id": 1,
                            "name": "Dump No.1",
                            "type": "DUMP"
                        },{
                            "id": 2,
                            "name": "Face No.1",
                            "type": "FACE"
                        },{
                            "id": 3,
                            "name": "Hole No.1",
                            "type": "HOLE"
                        },{
                            "id": 4,
                            "name": "Stockpile No.1",
                            "type": "STOCKPILE"
                        }]
                        """)));
        var webClient = newWebClient(wmRuntimeInfo);
        var sut = new RegistryClient(webClient);

        // when
        var locations = sut.getLocations().block();

        // then
        assertThat(locations).hasSize(4);
        assertThat(locations).hasAtLeastOneElementOfType(Dump.class);
        assertThat(locations).hasAtLeastOneElementOfType(Face.class);
        assertThat(locations).hasAtLeastOneElementOfType(Hole.class);
        assertThat(locations).hasAtLeastOneElementOfType(Stockpile.class);
        verify(getRequestedFor(urlPathEqualTo("/locations")));
    }

    WebClient newWebClient(WireMockRuntimeInfo wmRuntimeInfo) {
        return WebClient.builder()
                .baseUrl("http://localhost:" + wmRuntimeInfo.getHttpPort())
                .codecs(configurer -> new RegistryCodecConfigurer(objectMapperBuilder).configureCodecs(configurer))
                .build();
    }
}