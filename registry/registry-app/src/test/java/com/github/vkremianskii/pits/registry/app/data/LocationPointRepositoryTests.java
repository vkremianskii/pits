package com.github.vkremianskii.pits.registry.app.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.DUMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SpringBootTest
class LocationPointRepositoryTests {

    @Autowired
    LocationRepository locationRepository;
    @Autowired
    LocationPointRepository sut;

    @Test
    @Disabled("id returns null using H2 database")
    void should_insert_and_get_location_points() {
        // given
        locationRepository.insert("Dump No.1", DUMP).block();
        var locations = locationRepository.getLocations().block();
        var dump = locations.get(0);
        sut.insert(dump.id(), 1, 41.149320, -8.610143).block();
        sut.insert(dump.id(), 0, 41.149320, -8.610143).block();

        // when
        var points = sut.getPointsByLocationId(dump.id()).block();

        // then
        assertThat(points).hasSize(2);
        var point1 = points.get(0);
        assertThat(point1.locationId()).isEqualTo(dump.id());
        assertThat(point1.order()).isEqualTo(0);
        assertThat(point1.latitude()).isCloseTo(41.149320, offset(1e-6));
        assertThat(point1.longitude()).isCloseTo(-8.610143, offset(1e-6));
        var point2 = points.get(1);
        assertThat(point2.locationId()).isEqualTo(dump.id());
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
