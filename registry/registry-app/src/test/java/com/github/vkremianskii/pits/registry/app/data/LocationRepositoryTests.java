package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.types.model.location.Dump;
import com.github.vkremianskii.pits.registry.types.model.location.Face;
import com.github.vkremianskii.pits.registry.types.model.location.Hole;
import com.github.vkremianskii.pits.registry.types.model.location.Stockpile;
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
        assertThat(locations).hasAtLeastOneElementOfType(Dump.class);
        assertThat(locations).hasAtLeastOneElementOfType(Face.class);
        assertThat(locations).hasAtLeastOneElementOfType(Hole.class);
        assertThat(locations).hasAtLeastOneElementOfType(Stockpile.class);
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
    }
}
