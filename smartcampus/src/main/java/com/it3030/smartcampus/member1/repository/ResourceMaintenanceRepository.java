package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.ResourceMaintenance;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResourceMaintenanceRepository extends JpaRepository<ResourceMaintenance, Long> {
    
    List<ResourceMaintenance> findByResource(EnhancedResource resource);
    
    List<ResourceMaintenance> findByResourceId(Long resourceId);
    
    List<ResourceMaintenance> findByStatus(ResourceMaintenance.MaintenanceStatus status);
    
    @Query("SELECT m FROM ResourceMaintenance m WHERE m.scheduledDate <= :date AND m.status = 'SCHEDULED'")
    List<ResourceMaintenance> findOverdueMaintenance(@Param("date") LocalDateTime date);
    
    @Query("SELECT m FROM ResourceMaintenance m WHERE m.scheduledDate BETWEEN :startDate AND :endDate ORDER BY m.scheduledDate ASC")
    List<ResourceMaintenance> findMaintenanceInDateRange(@Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT m FROM ResourceMaintenance m WHERE m.technicianId = :technicianId ORDER BY m.scheduledDate DESC")
    List<ResourceMaintenance> findByTechnicianId(@Param("technicianId") String technicianId);
    
    @Query("SELECT m FROM ResourceMaintenance m WHERE m.maintenanceType = :type ORDER BY m.scheduledDate DESC")
    List<ResourceMaintenance> findByMaintenanceType(@Param("type") ResourceMaintenance.MaintenanceType type);
    
    @Query("SELECT m FROM ResourceMaintenance m WHERE m.status != 'COMPLETED' AND m.status != 'CANCELLED' ORDER BY m.scheduledDate ASC")
    List<ResourceMaintenance> findActiveMaintenance();
    
    @Query("SELECT COUNT(m) FROM ResourceMaintenance m WHERE m.status = 'COMPLETED' AND m.completedDate >= :date")
    Long countCompletedMaintenanceSince(@Param("date") LocalDateTime date);
    
    @Query("SELECT m FROM ResourceMaintenance m WHERE m.cost IS NOT NULL ORDER BY m.cost DESC")
    List<ResourceMaintenance> findMaintenanceWithCost();
    
    @Query("SELECT SUM(m.cost) FROM ResourceMaintenance m WHERE m.cost IS NOT NULL AND m.completedDate BETWEEN :startDate AND :endDate")
    Double calculateTotalMaintenanceCost(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
}
