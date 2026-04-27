package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.ResourceLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceLocationRepository extends JpaRepository<ResourceLocation, Long> {
    
    Optional<ResourceLocation> findByName(String name);
    
    boolean existsByName(String name);
    
    List<ResourceLocation> findByBuildingNameIgnoreCase(String buildingName);
    
    List<ResourceLocation> findByBuildingNameIgnoreCaseAndFloorNumber(String buildingName, Integer floorNumber);
    
    @Query("SELECT l FROM ResourceLocation l WHERE l.capacity >= ?1 ORDER BY l.capacity ASC")
    List<ResourceLocation> findByMinCapacity(Integer minCapacity);
    
    @Query("SELECT l FROM ResourceLocation l WHERE l.hasProjector = ?1 AND l.hasWifi = ?2 AND l.hasAirConditioning = ?3 ORDER BY l.buildingName, l.floorNumber")
    List<ResourceLocation> findByAmenities(Boolean hasProjector, Boolean hasWifi, Boolean hasAirConditioning);
    
    List<ResourceLocation> findByOrderByBuildingNameAscFloorNumberAscRoomNumberAsc();
    
    @Query("SELECT DISTINCT l.buildingName FROM ResourceLocation l ORDER BY l.buildingName")
    List<String> findAllBuildingNames();
}
