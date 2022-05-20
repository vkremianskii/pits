package com.github.vkremianskii.pits.registry.data;

import com.github.vkremianskii.pits.registry.model.location.Dump;
import com.github.vkremianskii.pits.registry.model.location.Face;
import com.github.vkremianskii.pits.registry.model.location.Hole;
import com.github.vkremianskii.pits.registry.model.location.Stockpile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        sut.put(new Dump(1, "Dump No.1")).block();
        sut.put(new Face(2, "Face No.1")).block();
        sut.put(new Hole(3, "Hole No.1")).block();
        sut.put(new Stockpile(4, "Stockpile No.1")).block();

        var locations = sut.getLocations().block();

        assertThat(locations).hasSize(4);
        assertThat(locations).hasAtLeastOneElementOfType(Dump.class);
        assertThat(locations).hasAtLeastOneElementOfType(Face.class);
        assertThat(locations).hasAtLeastOneElementOfType(Hole.class);
        assertThat(locations).hasAtLeastOneElementOfType(Stockpile.class);
    }
}
