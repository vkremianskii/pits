package com.github.vkremianskii.pits.registry.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.core.TestLocations.randomLocationId;
import static com.github.vkremianskii.pits.core.model.LocationType.DUMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
class LocationPointRepositoryTests {

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    LocationPointRepository sut;

    @Test
    void should_create_and_get_location_points() {
        // given
        var dumpId = randomLocationId();
        locationRepository.createLocation(dumpId, "Dump No.1", DUMP).block();
        sut.createLocationPoint(dumpId, 1, 41.149320, -8.610143).block();
        sut.createLocationPoint(dumpId, 0, 41.149320, -8.610143).block();

        // when
        var points = sut.getPointsByLocationId(dumpId).block();

        // then
        assertThat(points).hasSize(2);
        var point1 = points.get(0);
        assertThat(point1.locationId()).isEqualTo(dumpId);
        assertThat(point1.order()).isEqualTo(0);
        assertThat(point1.latitude()).isCloseTo(41.149320, offset(1e-6));
        assertThat(point1.longitude()).isCloseTo(-8.610143, offset(1e-6));
        var point2 = points.get(1);
        assertThat(point2.locationId()).isEqualTo(dumpId);
        assertThat(point2.order()).isEqualTo(1);
        assertThat(point2.latitude()).isCloseTo(41.149320, offset(1e-6));
        assertThat(point2.longitude()).isCloseTo(-8.610143, offset(1e-6));
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
        locationRepository.clear().block();
    }
}
