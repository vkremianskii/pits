package com.github.vkremianskii.pits.processes.functional;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.vkremianskii.pits.processes.data.EquipmentPayloadRepository;
import com.github.vkremianskii.pits.processes.data.EquipmentPositionRepository;
import com.github.vkremianskii.pits.processes.data.HaulCycleRepository;
import com.github.vkremianskii.pits.processes.job.HaulCycleJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@SpringBootTest(properties = {"integration.registry.baseUrl=http://localhost:18080"})
@WireMockTest(httpPort = 18080)
public class HaulCycleTests {

    @Autowired
    EquipmentPositionRepository positionRepository;
    @Autowired
    EquipmentPayloadRepository payloadRepository;
    @Autowired
    HaulCycleRepository haulCycleRepository;
    @Autowired
    HaulCycleJob haulCycleJob;

    @BeforeEach
    void cleanup() {
        positionRepository.clear().block();
        payloadRepository.clear().block();
        haulCycleRepository.clear().block();
    }

    @Test
    void should_compute_haul_cycles() {
        // given
        stubFor(get(urlPathEqualTo("/equipment")).willReturn(aResponse()
            .withStatus(200)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
            .withBody("""
                [{
                    "id": 1,
                    "name": "Shovel No.1",
                    "type": "SHOVEL",
                    "loadRadius": 20
                },{
                    "id": 2,
                    "name": "Truck No.1",
                    "type": "TRUCK"
                }]
                """)));
        stubFor(post(urlPathEqualTo("/equipment/2/state")).willReturn(aResponse()
            .withStatus(200)));
        haulCycleRepository.insert(2, 1, Instant.now(), null, null, null, null, null, null, null).block();

        // when
        haulCycleJob.computeHaulCycles();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment/2/state"))
            .withRequestBody(equalToJson("""
                {
                    "state": "WAIT_LOAD"
                }
                """)));
    }

    @Test
    void should_compute_haul_cycles__rollback_on_error() {
        // given
        stubFor(get(urlPathEqualTo("/equipment")).willReturn(aResponse()
            .withStatus(200)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
            .withBody("""
                [{
                    "id": 1,
                    "name": "Shovel No.1",
                    "type": "SHOVEL",
                    "loadRadius": 20
                },{
                    "id": 2,
                    "name": "Truck No.1",
                    "type": "TRUCK"
                }]
                """)));
        stubFor(post(urlPathEqualTo("/equipment/2/state")).willReturn(aResponse()
            .withStatus(500)));
        payloadRepository.insert(2, 20_000, Instant.ofEpochSecond(1)).block();

        // when
        haulCycleJob.computeHaulCycles();

        // then
        verify(postRequestedFor(urlPathEqualTo("/equipment/2/state"))
            .withRequestBody(equalToJson("""
                {
                    "state": "LOAD"
                }
                """)));
        var haulCycles = haulCycleRepository.getLastHaulCycleForTruck(2).block();
        assertThat(haulCycles).isEmpty();
    }
}
