package com.github.vkremianskii.pits.registry.api;

import com.github.vkremianskii.pits.registry.data.LocationRepository;
import com.github.vkremianskii.pits.registry.model.Location;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/location")
public class LocationController {
    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = requireNonNull(locationRepository);
    }

    @GetMapping
    public Mono<List<Location>> getLocations() {
        return locationRepository.getLocations();
    }
}
