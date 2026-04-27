package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.model.ResourceCategory;
import com.it3030.smartcampus.member1.model.ResourceLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnhancedResourceRepository extends JpaRepository<EnhancedResource, Long> {
    
    Optional<EnhancedResource> findByResourceCode(String resourceCode);
    
    boolean existsByResourceCode(String resourceCode);
    
    List<EnhancedResource> findByCategory(ResourceCategory category);
    
    List<EnhancedResource> findByLocation(ResourceLocation location);
    
    List<EnhancedResource> findByAvailableTrue();
    
    List<EnhancedResource> findByAvailableFalse();
    
    Page<EnhancedResource> findByAvailableTrue(Pageable pageable);
    
    @Query("SELECT r FROM EnhancedResource r WHERE r.name LIKE %:name% OR r.description LIKE %:name%")
    Page<EnhancedResource> findByNameOrDescriptionContaining(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT r FROM EnhancedResource r WHERE r.category.id = :categoryId AND r.location.id = :locationId")
    List<EnhancedResource> findByCategoryAndLocation(@Param("categoryId") Long categoryId, @Param("locationId") Long locationId);
    
    @Query("SELECT r FROM EnhancedResource r WHERE r.maxCapacity >= :minCapacity")
    List<EnhancedResource> findByMinCapacity(@Param("minCapacity") Integer minCapacity);
    
    @Query("SELECT r FROM EnhancedResource r WHERE r.averageRating >= :minRating ORDER BY r.averageRating DESC")
    List<EnhancedResource> findByMinRating(@Param("minRating") Double minRating);
    
    @Query("SELECT r FROM EnhancedResource r WHERE r.maintenanceStatus = 'GOOD' AND r.available = true")
    List<EnhancedResource> findAvailableAndInGoodCondition();
    
    @Query("SELECT r FROM EnhancedResource r WHERE r.nextMaintenanceDate <= :date")
    List<EnhancedResource> findResourcesNeedingMaintenance(@Param("date") LocalDateTime date);
    
    @Query("SELECT r FROM EnhancedResource r WHERE r.usageCount >= :minUsage ORDER BY r.usageCount DESC")
    List<EnhancedResource> findMostUsedResources(@Param("minUsage") Integer minUsage);
    
    @Query("SELECT r FROM EnhancedResource r WHERE r.bookingCount >= :minBookings ORDER BY r.bookingCount DESC")
    List<EnhancedResource> findMostBookedResources(@Param("minBookings") Integer minBookings);
    
    @Query("SELECT COUNT(r) FROM EnhancedResource r WHERE r.available = true")
    long countAvailableResources();
    
    @Query("SELECT COUNT(r) FROM EnhancedResource r WHERE r.available = false")
    long countUnavailableResources();
    
    @Query("SELECT r FROM EnhancedResource r ORDER BY r.averageRating DESC")
    List<EnhancedResource> findTopRatedResources();
    
    @Query("SELECT r FROM EnhancedResource r ORDER BY r.usageCount DESC")
    List<EnhancedResource> findMostUsedResources();
}
