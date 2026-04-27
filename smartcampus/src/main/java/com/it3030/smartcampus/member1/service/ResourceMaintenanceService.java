package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourceMaintenance;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.repository.ResourceMaintenanceRepository;
import com.it3030.smartcampus.member1.repository.EnhancedResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceMaintenanceService {

    @Autowired
    private ResourceMaintenanceRepository maintenanceRepository;

    @Autowired
    private EnhancedResourceRepository resourceRepository;

    public List<ResourceMaintenance> getAllMaintenanceRecords() {
        return maintenanceRepository.findAll();
    }

    public Optional<ResourceMaintenance> getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id);
    }

    public List<ResourceMaintenance> getMaintenanceByResourceId(Long resourceId) {
        return maintenanceRepository.findByResourceId(resourceId);
    }

    public ResourceMaintenance scheduleMaintenance(ResourceMaintenance maintenance) {
        // Validate resource exists
        EnhancedResource resource = resourceRepository.findById(maintenance.getResource().getId())
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + maintenance.getResource().getId()));

        // Validate scheduled date is in the future
        if (maintenance.getScheduledDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Scheduled date must be in the future");
        }

        maintenance.setResource(resource);
        maintenance.setCreatedAt(LocalDateTime.now());
        maintenance.setUpdatedAt(LocalDateTime.now());
        maintenance.setStatus(ResourceMaintenance.MaintenanceStatus.SCHEDULED);

        // Update resource next maintenance date
        resource.setNextMaintenanceDate(maintenance.getScheduledDate());
        resourceRepository.save(resource);

        return maintenanceRepository.save(maintenance);
    }

    public ResourceMaintenance updateMaintenance(Long id, ResourceMaintenance maintenanceDetails) {
        ResourceMaintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));

        maintenance.setMaintenanceType(maintenanceDetails.getMaintenanceType());
        maintenance.setDescription(maintenanceDetails.getDescription());
        maintenance.setScheduledDate(maintenanceDetails.getScheduledDate());
        maintenance.setTechnicianId(maintenanceDetails.getTechnicianId());
        maintenance.setTechnicianName(maintenanceDetails.getTechnicianName());
        maintenance.setCost(maintenanceDetails.getCost());
        maintenance.setNotes(maintenanceDetails.getNotes());
        maintenance.setUpdatedAt(LocalDateTime.now());

        return maintenanceRepository.save(maintenance);
    }

    public ResourceMaintenance startMaintenance(Long id, String technicianId, String technicianName) {
        ResourceMaintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));

        if (maintenance.getStatus() != ResourceMaintenance.MaintenanceStatus.SCHEDULED) {
            throw new IllegalStateException("Maintenance can only be started from SCHEDULED status");
        }

        maintenance.setStatus(ResourceMaintenance.MaintenanceStatus.IN_PROGRESS);
        maintenance.setTechnicianId(technicianId);
        maintenance.setTechnicianName(technicianName);
        maintenance.setUpdatedAt(LocalDateTime.now());

        // Update resource maintenance status
        EnhancedResource resource = maintenance.getResource();
        resource.setMaintenanceStatus(EnhancedResource.MaintenanceStatus.UNDER_MAINTENANCE);
        resourceRepository.save(resource);

        return maintenanceRepository.save(maintenance);
    }

    public ResourceMaintenance completeMaintenance(Long id, String notes, Double cost) {
        ResourceMaintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));

        if (maintenance.getStatus() != ResourceMaintenance.MaintenanceStatus.IN_PROGRESS) {
            throw new IllegalStateException("Maintenance can only be completed from IN_PROGRESS status");
        }

        maintenance.setStatus(ResourceMaintenance.MaintenanceStatus.COMPLETED);
        maintenance.setCompletedDate(LocalDateTime.now());
        maintenance.setNotes(notes);
        if (cost != null) {
            maintenance.setCost(cost);
        }
        maintenance.setUpdatedAt(LocalDateTime.now());

        // Update resource maintenance status and last maintenance date
        EnhancedResource resource = maintenance.getResource();
        resource.setMaintenanceStatus(EnhancedResource.MaintenanceStatus.GOOD);
        resource.setLastMaintenanceDate(LocalDateTime.now());
        resourceRepository.save(resource);

        return maintenanceRepository.save(maintenance);
    }

    public ResourceMaintenance cancelMaintenance(Long id, String reason) {
        ResourceMaintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));

        if (maintenance.getStatus() == ResourceMaintenance.MaintenanceStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed maintenance");
        }

        maintenance.setStatus(ResourceMaintenance.MaintenanceStatus.CANCELLED);
        maintenance.setNotes(reason);
        maintenance.setUpdatedAt(LocalDateTime.now());

        // Update resource maintenance status back to GOOD if it was under maintenance
        EnhancedResource resource = maintenance.getResource();
        if (resource.getMaintenanceStatus() == EnhancedResource.MaintenanceStatus.UNDER_MAINTENANCE) {
            resource.setMaintenanceStatus(EnhancedResource.MaintenanceStatus.GOOD);
            resourceRepository.save(resource);
        }

        return maintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(Long id) {
        ResourceMaintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));

        // Cannot delete completed maintenance records
        if (maintenance.getStatus() == ResourceMaintenance.MaintenanceStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete completed maintenance records");
        }

        maintenanceRepository.delete(maintenance);
    }

    public List<ResourceMaintenance> getMaintenanceByStatus(ResourceMaintenance.MaintenanceStatus status) {
        return maintenanceRepository.findByStatus(status);
    }

    public List<ResourceMaintenance> getOverdueMaintenance() {
        return maintenanceRepository.findOverdueMaintenance(LocalDateTime.now());
    }

    public List<ResourceMaintenance> getMaintenanceInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return maintenanceRepository.findMaintenanceInDateRange(startDate, endDate);
    }

    public List<ResourceMaintenance> getMaintenanceByTechnician(String technicianId) {
        return maintenanceRepository.findByTechnicianId(technicianId);
    }

    public List<ResourceMaintenance> getMaintenanceByType(ResourceMaintenance.MaintenanceType type) {
        return maintenanceRepository.findByMaintenanceType(type);
    }

    public List<ResourceMaintenance> getActiveMaintenance() {
        return maintenanceRepository.findActiveMaintenance();
    }

    public Long countCompletedMaintenanceSince(LocalDateTime date) {
        return maintenanceRepository.countCompletedMaintenanceSince(date);
    }

    public List<ResourceMaintenance> getMaintenanceWithCost() {
        return maintenanceRepository.findMaintenanceWithCost();
    }

    public Double calculateTotalMaintenanceCost(LocalDateTime startDate, LocalDateTime endDate) {
        return maintenanceRepository.calculateTotalMaintenanceCost(startDate, endDate);
    }

    public void checkAndUpdateOverdueMaintenance() {
        List<ResourceMaintenance> overdueMaintenance = getOverdueMaintenance();
        
        for (ResourceMaintenance maintenance : overdueMaintenance) {
            maintenance.setStatus(ResourceMaintenance.MaintenanceStatus.OVERDUE);
            maintenance.setUpdatedAt(LocalDateTime.now());
            maintenanceRepository.save(maintenance);

            // Update resource maintenance status
            EnhancedResource resource = maintenance.getResource();
            resource.setMaintenanceStatus(EnhancedResource.MaintenanceStatus.NEEDS_ATTENTION);
            resourceRepository.save(resource);
        }
    }

    public ResourceMaintenance createEmergencyMaintenance(Long resourceId, String description, String technicianId, String technicianName) {
        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        ResourceMaintenance emergencyMaintenance = new ResourceMaintenance(
            resource, 
            ResourceMaintenance.MaintenanceType.EMERGENCY, 
            description, 
            LocalDateTime.now()
        );

        emergencyMaintenance.setTechnicianId(technicianId);
        emergencyMaintenance.setTechnicianName(technicianName);
        emergencyMaintenance.setStatus(ResourceMaintenance.MaintenanceStatus.IN_PROGRESS);
        emergencyMaintenance.setCreatedAt(LocalDateTime.now());
        emergencyMaintenance.setUpdatedAt(LocalDateTime.now());

        // Update resource status immediately
        resource.setMaintenanceStatus(EnhancedResource.MaintenanceStatus.UNDER_MAINTENANCE);
        resourceRepository.save(resource);

        return maintenanceRepository.save(emergencyMaintenance);
    }
}
