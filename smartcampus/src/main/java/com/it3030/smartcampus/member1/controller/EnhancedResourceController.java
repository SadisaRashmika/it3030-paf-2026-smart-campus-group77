package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.service.EnhancedResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/enhanced-resources")
public class EnhancedResourceController {

    @Autowired
    private EnhancedResourceService resourceService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<EnhancedResource> getAllAvailableResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return resourceService.getAllAvailableResources(pageable);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<EnhancedResource> getAllResources() {
        return resourceService.getAllResources();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnhancedResource> getResourceById(@PathVariable Long id) {
        Optional<EnhancedResource> resource = resourceService.getResourceById(id);
        return resource.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{resourceCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnhancedResource> getResourceByCode(@PathVariable String resourceCode) {
        Optional<EnhancedResource> resource = resourceService.getResourceByCode(resourceCode);
        return resource.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public EnhancedResource createResource(@RequestBody EnhancedResource resource) {
        return resourceService.createResource(resource);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<EnhancedResource> updateResource(@PathVariable Long id, @RequestBody EnhancedResource resourceDetails) {
        try {
            EnhancedResource updatedResource = resourceService.updateResource(id, resourceDetails);
            return ResponseEntity.ok(updatedResource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        try {
            resourceService.deleteResource(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public Page<EnhancedResource> searchResources(
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return resourceService.searchResources(term, pageable);
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    public List<EnhancedResource> findByCategory(@PathVariable Long categoryId) {
        return resourceService.findByCategory(categoryId);
    }

    @GetMapping("/location/{locationId}")
    @PreAuthorize("isAuthenticated()")
    public List<EnhancedResource> findByLocation(@PathVariable Long locationId) {
        return resourceService.findByLocation(locationId);
    }

    @GetMapping("/capacity/{minCapacity}")
    @PreAuthorize("isAuthenticated()")
    public List<EnhancedResource> findByMinCapacity(@PathVariable Integer minCapacity) {
        return resourceService.findByMinCapacity(minCapacity);
    }

    @GetMapping("/rating/{minRating}")
    @PreAuthorize("isAuthenticated()")
    public List<EnhancedResource> findByMinRating(@PathVariable Double minRating) {
        return resourceService.findByMinRating(minRating);
    }

    @GetMapping("/available-good")
    @PreAuthorize("isAuthenticated()")
    public List<EnhancedResource> findAvailableAndInGoodCondition() {
        return resourceService.findAvailableAndInGoodCondition();
    }

    @GetMapping("/maintenance-needed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<EnhancedResource> findResourcesNeedingMaintenance() {
        return resourceService.findResourcesNeedingMaintenance(java.time.LocalDateTime.now());
    }

    @GetMapping("/most-used/{minUsage}")
    @PreAuthorize("isAuthenticated()")
    public List<EnhancedResource> findMostUsedResources(@PathVariable Integer minUsage) {
        return resourceService.findMostUsedResources(minUsage);
    }

    @GetMapping("/most-booked/{minBookings}")
    @PreAuthorize("isAuthenticated()")
    public List<EnhancedResource> findMostBookedResources(@PathVariable Integer minBookings) {
        return resourceService.findMostBookedResources(minBookings);
    }

    @GetMapping("/top-rated")
    @PreAuthorize("isAuthenticated()")
    public List<EnhancedResource> findTopRatedResources() {
        return resourceService.findTopRatedResources();
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Object> getResourceStatistics() {
        long availableCount = resourceService.countAvailableResources();
        long unavailableCount = resourceService.countUnavailableResources();
        
        return ResponseEntity.ok(java.util.Map.of(
            "available", availableCount,
            "unavailable", unavailableCount,
            "total", availableCount + unavailableCount
        ));
    }

    @PostMapping("/{id}/increment-usage")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnhancedResource> incrementUsageCount(@PathVariable Long id) {
        try {
            EnhancedResource resource = resourceService.incrementUsageCount(id);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/increment-booking")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnhancedResource> incrementBookingCount(@PathVariable Long id) {
        try {
            EnhancedResource resource = resourceService.incrementBookingCount(id);
            return ResponseEntity.ok(resource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/maintenance-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<EnhancedResource> updateMaintenanceStatus(
            @PathVariable Long id,
            @RequestParam EnhancedResource.MaintenanceStatus status,
            @RequestParam(required = false) String nextMaintenanceDateStr) {
        
        try {
            java.time.LocalDateTime nextMaintenanceDate = null;
            if (nextMaintenanceDateStr != null && !nextMaintenanceDateStr.isEmpty()) {
                nextMaintenanceDate = java.time.LocalDateTime.parse(nextMaintenanceDateStr);
            }
            
            EnhancedResource resource = resourceService.updateMaintenanceStatus(id, status, nextMaintenanceDate);
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/exists/{resourceCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Boolean> checkResourceExists(@PathVariable String resourceCode) {
        boolean exists = resourceService.getResourceByCode(resourceCode).isPresent();
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/seed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> seedDefaultResources() {
        resourceService.seedDefaultResources();
        return ResponseEntity.ok("Default resources seeded successfully");
    }
}
