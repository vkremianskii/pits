package com.github.vkremianskii.pits.registry.app.data;

import com.github.vkremianskii.pits.registry.types.model.LocationDeclaration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.vkremianskii.pits.core.types.TestLocations.randomLocationId;
import static com.github.vkremianskii.pits.core.types.model.LocationType.DUMP;
import static com.github.vkremianskii.pits.core.types.model.LocationType.FACE;
import static com.github.vkremianskii.pits.core.types.model.LocationType.HOLE;
import static com.github.vkremianskii.pits.core.types.model.LocationType.STOCKPILE;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LocationRepositoryTests {

    @Autowired
    LocationRepository sut;

    @Test
    void should_create_and_get_locations() {
        // when
        var dumpId = randomLocationId();
        sut.createLocation(dumpId, "Dump No.1", DUMP).block();
        var faceId = randomLocationId();
        sut.createLocation(faceId, "Face No.1", FACE).block();
        var holeId = randomLocationId();
        sut.createLocation(holeId, "Hole No.1", HOLE).block();
        var stockpileId = randomLocationId();
        sut.createLocation(stockpileId, "Stockpile No.1", STOCKPILE).block();
        var locations = sut.getLocations().block();

        // then
        var locationById = locations.stream().collect(toMap(LocationDeclaration::id, identity()));
        var dump = locationById.get(dumpId);
        assertThat(dump.name()).isEqualTo("Dump No.1");
        assertThat(dump.type()).isEqualTo(DUMP);
        var face = locationById.get(faceId);
        assertThat(face.name()).isEqualTo("Face No.1");
        assertThat(face.type()).isEqualTo(FACE);
        var hole = locationById.get(holeId);
        assertThat(hole.name()).isEqualTo("Hole No.1");
        assertThat(hole.type()).isEqualTo(HOLE);
        var stockpile = locationById.get(stockpileId);
        assertThat(stockpile.name()).isEqualTo("Stockpile No.1");
        assertThat(stockpile.type()).isEqualTo(STOCKPILE);
    }

    @AfterEach
    void cleanup() {
        sut.clear().block();
    }
}
