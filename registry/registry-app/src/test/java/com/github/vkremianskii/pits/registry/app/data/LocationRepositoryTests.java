package com.github.vkremianskii.pits.registry.app.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.registry.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.registry.types.model.LocationType.STOCKPILE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocationRepositoryTests {

    @Autowired
    LocationRepository sut;

    @Test
    void should_insert_and_get_locations() {
        // when
        sut.insert("Dump No.1", DUMP).block();
        sut.insert("Face No.1", FACE).block();
        sut.insert("Hole No.1", HOLE).block();
        sut.insert("Stockpile No.1", STOCKPILE).block();
        var locations = sut.getLocations().block();

        // then
        assertThat(locations).hasSize(4);
        var location1 = locations.get(0);
        assertThat(location1.type()).isEqualTo(DUMP);
        var location2 = locations.get(1);
        assertThat(location2.type()).isEqualTo(FACE);
        var location3 = locations.get(2);
        assertThat(location3.type()).isEqualTo(HOLE);
        var location4 = locations.get(3);
        assertThat(location4.type()).isEqualTo(STOCKPILE);
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
    }
}
