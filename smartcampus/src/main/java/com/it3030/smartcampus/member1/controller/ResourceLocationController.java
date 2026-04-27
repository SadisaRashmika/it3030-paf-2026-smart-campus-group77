package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourceLocation;
import com.it3030.smartcampus.member1.service.ResourceLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/locations")
public class ResourceLocationController {

    @Autowired
    private ResourceLocationService locationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ResourceLocation> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceLocation> getLocationById(@PathVariable Long id) {
        Optional<ResourceLocation> location = locationService.getLocationById(id);
        return location.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceLocation> getLocationByName(@PathVariable String name) {
        Optional<ResourceLocation> location = locationService.getLocationByName(name);
        return location.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResourceLocation createLocation(@RequestBody ResourceLocation location) {
        return locationService.createLocation(location);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourceLocation> updateLocation(@PathVariable Long id, @RequestBody ResourceLocation locationDetails) {
        try {
            ResourceLocation updatedLocation = locationService.updateLocation(id, locationDetails);
            return ResponseEntity.ok(updatedLocation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceLocation> searchLocations(@RequestParam String term) {
        return locationService.searchLocations(term);
    }

    @GetMapping("/building/{buildingName}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceLocation> findByBuilding(@PathVariable String buildingName) {
        return locationService.findByBuilding(buildingName);
    }

    @GetMapping("/building/{buildingName}/floor/{floorNumber}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceLocation> findByBuildingAndFloor(@PathVariable String buildingName, @PathVariable Integer floorNumber) {
        return locationService.findByBuildingAndFloor(buildingName, floorNumber);
    }

    @GetMapping("/capacity/{minCapacity}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceLocation> findByMinCapacity(@PathVariable Integer minCapacity) {
        return locationService.findByMinCapacity(minCapacity);
    }

    @GetMapping("/amenities")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceLocation> findByAmenities(
            @RequestParam(required = false) Boolean hasProjector,
            @RequestParam(required = false) Boolean hasWifi,
            @RequestParam(required = false) Boolean hasAirConditioning) {
        
        // Default to true if not specified for better filtering
        Boolean projector = hasProjector != null ? hasProjector : false;
        Boolean wifi = hasWifi != null ? hasWifi : true;
        Boolean ac = hasAirConditioning != null ? hasAirConditioning : false;
        
        return locationService.findByAmenities(projector, wifi, ac);
    }

    @GetMapping("/buildings")
    @PreAuthorize("isAuthenticated()")
    public List<String> getAllBuildingNames() {
        return locationService.getAllBuildingNames();
    }

    @GetMapping("/exists/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Boolean> checkLocationExists(@PathVariable String name) {
        boolean exists = locationService.locationExists(name);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/seed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> seedDefaultLocations() {
        locationService.seedDefaultLocations();
        return ResponseEntity.ok("Default locations seeded successfully");
    }
}
