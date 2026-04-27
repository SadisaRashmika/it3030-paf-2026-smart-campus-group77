package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourceLocation;
import com.it3030.smartcampus.member1.repository.ResourceLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceLocationService {

    @Autowired
    private ResourceLocationRepository locationRepository;

    public List<ResourceLocation> getAllLocations() {
        return locationRepository.findByOrderByBuildingNameAscFloorNumberAscRoomNumberAsc();
    }

    public Optional<ResourceLocation> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public Optional<ResourceLocation> getLocationByName(String name) {
        return locationRepository.findByName(name);
    }

    public ResourceLocation createLocation(ResourceLocation location) {
        if (locationRepository.existsByName(location.getName())) {
            throw new IllegalArgumentException("Location with name '" + location.getName() + "' already exists");
        }
        
        location.setCreatedAt(LocalDateTime.now());
        location.setUpdatedAt(LocalDateTime.now());
        
        return locationRepository.save(location);
    }

    public ResourceLocation updateLocation(Long id, ResourceLocation locationDetails) {
        ResourceLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!location.getName().equals(locationDetails.getName()) && 
            locationRepository.existsByName(locationDetails.getName())) {
            throw new IllegalArgumentException("Location with name '" + locationDetails.getName() + "' already exists");
        }

        location.setName(locationDetails.getName());
        location.setDescription(locationDetails.getDescription());
        location.setBuildingName(locationDetails.getBuildingName());
        location.setFloorNumber(locationDetails.getFloorNumber());
        location.setRoomNumber(locationDetails.getRoomNumber());
        location.setCapacity(locationDetails.getCapacity());
        location.setHasProjector(locationDetails.getHasProjector());
        location.setHasWifi(locationDetails.getHasWifi());
        location.setHasAirConditioning(locationDetails.getHasAirConditioning());
        location.setIsAccessible(locationDetails.getIsAccessible());
        location.setUpdatedAt(LocalDateTime.now());

        return locationRepository.save(location);
    }

    public void deleteLocation(Long id) {
        ResourceLocation location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        // Resources relationship removed - skip this check

        locationRepository.delete(location);
    }

    public List<ResourceLocation> searchLocations(String searchTerm) {
        return locationRepository.findByBuildingNameIgnoreCase(searchTerm);
    }

    public List<ResourceLocation> findByBuilding(String buildingName) {
        return locationRepository.findByBuildingNameIgnoreCase(buildingName);
    }

    public List<ResourceLocation> findByBuildingAndFloor(String buildingName, Integer floorNumber) {
        return locationRepository.findByBuildingNameIgnoreCaseAndFloorNumber(buildingName, floorNumber);
    }

    public List<ResourceLocation> findByMinCapacity(Integer minCapacity) {
        return locationRepository.findByMinCapacity(minCapacity);
    }

    public List<ResourceLocation> findByAmenities(Boolean hasProjector, Boolean hasWifi, Boolean hasAirConditioning) {
        return locationRepository.findByAmenities(hasProjector, hasWifi, hasAirConditioning);
    }

    public List<String> getAllBuildingNames() {
        return locationRepository.findAllBuildingNames();
    }

    public boolean locationExists(String name) {
        return locationRepository.existsByName(name);
    }

    public void seedDefaultLocations() {
        if (locationRepository.count() == 0) {
            // Main Building
            createLocation(new ResourceLocation("Main Hall 101", "Large lecture hall", "Main Building", 1, "101", 200));
            createLocation(new ResourceLocation("Computer Lab 201", "Computer laboratory", "Main Building", 2, "201", 50));
            createLocation(new ResourceLocation("Meeting Room 301", "Small meeting room", "Main Building", 3, "301", 15));
            createLocation(new ResourceLocation("Library Study Area", "Quiet study area", "Main Building", 4, "401", 100));
            
            // Science Building
            createLocation(new ResourceLocation("Physics Lab 101", "Physics laboratory", "Science Building", 1, "101", 30));
            createLocation(new ResourceLocation("Chemistry Lab 201", "Chemistry laboratory", "Science Building", 2, "201", 25));
            createLocation(new ResourceLocation("Biology Lab 301", "Biology laboratory", "Science Building", 3, "301", 35));
            
            // Sports Complex
            createLocation(new ResourceLocation("Gymnasium", "Indoor sports facility", "Sports Complex", 1, "Gym", 150));
            createLocation(new ResourceLocation("Fitness Center", "Weight training area", "Sports Complex", 1, "Fitness", 40));
            createLocation(new ResourceLocation("Swimming Pool", "Olympic size pool", "Sports Complex", 2, "Pool", 200));
            
            // Administration Building
            createLocation(new ResourceLocation("Auditorium", "Main auditorium", "Administration Building", 1, "Auditorium", 500));
            createLocation(new ResourceLocation("Conference Room A", "Large conference room", "Administration Building", 2, "A", 30));
            createLocation(new ResourceLocation("Conference Room B", "Small conference room", "Administration Building", 2, "B", 20));
        }
    }
}
