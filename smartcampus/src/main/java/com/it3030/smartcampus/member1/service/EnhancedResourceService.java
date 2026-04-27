package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.model.ResourceCategory;
import com.it3030.smartcampus.member1.model.ResourceLocation;
import com.it3030.smartcampus.member1.repository.EnhancedResourceRepository;
import com.it3030.smartcampus.member1.repository.ResourceCategoryRepository;
import com.it3030.smartcampus.member1.repository.ResourceLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class EnhancedResourceService {

    @Autowired
    private EnhancedResourceRepository resourceRepository;

    @Autowired
    private ResourceCategoryRepository categoryRepository;

    @Autowired
    private ResourceLocationRepository locationRepository;

    public Page<EnhancedResource> getAllAvailableResources(Pageable pageable) {
        return resourceRepository.findByAvailableTrue(pageable);
    }

    public List<EnhancedResource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Optional<EnhancedResource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public Optional<EnhancedResource> getResourceByCode(String resourceCode) {
        return resourceRepository.findByResourceCode(resourceCode);
    }

    public EnhancedResource createResource(EnhancedResource resource) {
        // Validate category and location exist
        ResourceCategory category = categoryRepository.findById(resource.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + resource.getCategory().getId()));

        ResourceLocation location = locationRepository.findById(resource.getLocation().getId())
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + resource.getLocation().getId()));

        // Generate unique resource code if not provided
        if (resource.getResourceCode() == null || resource.getResourceCode().isEmpty()) {
            resource.setResourceCode(generateUniqueResourceCode());
        } else if (resourceRepository.existsByResourceCode(resource.getResourceCode())) {
            throw new IllegalArgumentException("Resource with code '" + resource.getResourceCode() + "' already exists");
        }

        resource.setCategory(category);
        resource.setLocation(location);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        resource.setUsageCount(0);
        resource.setBookingCount(0);
        resource.setTotalRatings(0);
        resource.setAverageRating(0.0);

        return resourceRepository.save(resource);
    }

    public EnhancedResource updateResource(Long id, EnhancedResource resourceDetails) {
        EnhancedResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));

        // Validate category and location if they're being changed
        if (resourceDetails.getCategory() != null && !resourceDetails.getCategory().getId().equals(resource.getCategory().getId())) {
            ResourceCategory category = categoryRepository.findById(resourceDetails.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + resourceDetails.getCategory().getId()));
            resource.setCategory(category);
        }

        if (resourceDetails.getLocation() != null && !resourceDetails.getLocation().getId().equals(resource.getLocation().getId())) {
            ResourceLocation location = locationRepository.findById(resourceDetails.getLocation().getId())
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + resourceDetails.getLocation().getId()));
            resource.setLocation(location);
        }

        // Check if resource code is being changed and if new code already exists
        if (resourceDetails.getResourceCode() != null && 
            !resourceDetails.getResourceCode().equals(resource.getResourceCode()) && 
            resourceRepository.existsByResourceCode(resourceDetails.getResourceCode())) {
            throw new IllegalArgumentException("Resource with code '" + resourceDetails.getResourceCode() + "' already exists");
        }

        resource.setName(resourceDetails.getName());
        resource.setDescription(resourceDetails.getDescription());
        resource.setType(resourceDetails.getType());
        resource.setResourceCode(resourceDetails.getResourceCode());
        resource.setAvailable(resourceDetails.getAvailable());
        resource.setMaxCapacity(resourceDetails.getMaxCapacity());
        resource.setCurrentCapacity(resourceDetails.getCurrentCapacity());
        resource.setEquipmentList(resourceDetails.getEquipmentList());
        resource.setUsageRules(resourceDetails.getUsageRules());
        resource.setMaintenanceStatus(resourceDetails.getMaintenanceStatus());
        resource.setImageUrl(resourceDetails.getImageUrl());
        resource.setUpdatedAt(LocalDateTime.now());

        return resourceRepository.save(resource);
    }

    public void deleteResource(Long id) {
        EnhancedResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));

        resourceRepository.delete(resource);
    }

    public Page<EnhancedResource> searchResources(String searchTerm, Pageable pageable) {
        return resourceRepository.findByNameOrDescriptionContaining(searchTerm, pageable);
    }

    public List<EnhancedResource> findByCategory(Long categoryId) {
        ResourceCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        return resourceRepository.findByCategory(category);
    }

    public List<EnhancedResource> findByLocation(Long locationId) {
        ResourceLocation location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));
        return resourceRepository.findByLocation(location);
    }

    public List<EnhancedResource> findByMinCapacity(Integer minCapacity) {
        return resourceRepository.findByMinCapacity(minCapacity);
    }

    public List<EnhancedResource> findByMinRating(Double minRating) {
        return resourceRepository.findByMinRating(minRating);
    }

    public List<EnhancedResource> findAvailableAndInGoodCondition() {
        return resourceRepository.findAvailableAndInGoodCondition();
    }

    public List<EnhancedResource> findResourcesNeedingMaintenance(LocalDateTime date) {
        return resourceRepository.findResourcesNeedingMaintenance(date);
    }

    public List<EnhancedResource> findMostUsedResources(Integer minUsage) {
        return resourceRepository.findMostUsedResources(minUsage);
    }

    public List<EnhancedResource> findMostBookedResources(Integer minBookings) {
        return resourceRepository.findMostBookedResources(minBookings);
    }

    public List<EnhancedResource> findTopRatedResources() {
        return resourceRepository.findTopRatedResources();
    }

    public long countAvailableResources() {
        return resourceRepository.countAvailableResources();
    }

    public long countUnavailableResources() {
        return resourceRepository.countUnavailableResources();
    }

    public EnhancedResource incrementUsageCount(Long resourceId) {
        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));
        
        resource.setUsageCount(resource.getUsageCount() + 1);
        resource.setUpdatedAt(LocalDateTime.now());
        
        return resourceRepository.save(resource);
    }

    public EnhancedResource incrementBookingCount(Long resourceId) {
        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));
        
        resource.setBookingCount(resource.getBookingCount() + 1);
        resource.setUpdatedAt(LocalDateTime.now());
        
        return resourceRepository.save(resource);
    }

    public EnhancedResource updateResourceRating(Long resourceId, Double newAverageRating, Integer totalRatings) {
        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));
        
        resource.setAverageRating(newAverageRating);
        resource.setTotalRatings(totalRatings);
        resource.setUpdatedAt(LocalDateTime.now());
        
        return resourceRepository.save(resource);
    }

    public EnhancedResource updateMaintenanceStatus(Long resourceId, EnhancedResource.MaintenanceStatus status, LocalDateTime nextMaintenanceDate) {
        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));
        
        resource.setMaintenanceStatus(status);
        if (nextMaintenanceDate != null) {
            resource.setNextMaintenanceDate(nextMaintenanceDate);
        }
        resource.setUpdatedAt(LocalDateTime.now());
        
        return resourceRepository.save(resource);
    }

    private String generateUniqueResourceCode() {
        String prefix = "RES";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8);
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp + random;
    }

    public void seedDefaultResources() {
        if (resourceRepository.count() == 0) {
            // This would be populated after categories and locations are seeded
            // Implementation would depend on the seeded categories and locations
        }
    }
}
