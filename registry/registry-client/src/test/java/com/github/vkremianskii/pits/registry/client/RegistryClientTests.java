package com.github.vkremianskii.pits.registry.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
class RegistryClientTests {
    static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void should_get_equipment(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(get(urlPathEqualTo("/equipment")).willReturn(aResponse()
                .withStatus(200)
                .withJsonBody(objectMapper.createObjectNode()
                        .put("id", 1)
                        .put("name", "Dozer No.1")
                        .put("type", "DOZER"))));
        var webClient = WebClient.builder()
                .baseUrl("http://localhost:" + wmRuntimeInfo.getHttpPort())
                .build();
        var sut = new RegistryClient(webClient);

        // when
        var equipment = sut.getEquipment().block();

        // then
        assertThat(equipment).hasSize(1);
        verify(getRequestedFor(urlPathEqualTo("/equipment")));
    }

    @Test
    void should_get_locations(WireMockRuntimeInfo wmRuntimeInfo) {
        // given
        stubFor(get(urlPathEqualTo("/locations")).willReturn(aResponse()
                .withStatus(200)
                .withJsonBody(objectMapper.createObjectNode()
                        .put("id", 1)
                        .put("name", "Dump No.1")
                        .put("type", "DUMP"))));
        var webClient = WebClient.builder()
                .baseUrl("http://localhost:" + wmRuntimeInfo.getHttpPort())
                .build();
        var sut = new RegistryClient(webClient);

        // when
        var locations = sut.getLocations().block();

        // then
        assertThat(locations).hasSize(1);
        verify(getRequestedFor(urlPathEqualTo("/locations")));
    }
}
