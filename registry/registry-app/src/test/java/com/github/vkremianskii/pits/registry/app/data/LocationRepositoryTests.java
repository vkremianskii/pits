package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.app.data.LocationRepository;
import com.github.vkremianskii.pits.registry.app.model.location.Dump;
import com.github.vkremianskii.pits.registry.app.model.location.Face;
import com.github.vkremianskii.pits.registry.app.model.location.Hole;
import com.github.vkremianskii.pits.registry.app.model.location.Stockpile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.registry.app.model.LocationType.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocationRepositoryTests {
    @Autowired
    LocationRepository sut;

    @BeforeEach
    void cleanup() {
        sut.clear().block();
    }

    @Test
    void should_put_and_get_locations() {
        // given
        sut.put( "Dump No.1", DUMP).block();
        sut.put("Face No.1", FACE).block();
        sut.put("Hole No.1", HOLE).block();
        sut.put("Stockpile No.1", STOCKPILE).block();

        // when
        var locations = sut.getLocations().block();

        // then
        assertThat(locations).hasSize(4);
        assertThat(locations).hasAtLeastOneElementOfType(Dump.class);
        assertThat(locations).hasAtLeastOneElementOfType(Face.class);
        assertThat(locations).hasAtLeastOneElementOfType(Hole.class);
        assertThat(locations).hasAtLeastOneElementOfType(Stockpile.class);
    }
}
