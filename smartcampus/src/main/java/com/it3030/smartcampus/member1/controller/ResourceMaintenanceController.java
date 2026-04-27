package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourceMaintenance;
import com.it3030.smartcampus.member1.service.ResourceMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/maintenance")
public class ResourceMaintenanceController {

    @Autowired
    private ResourceMaintenanceService maintenanceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public List<ResourceMaintenance> getAllMaintenanceRecords() {
        return maintenanceService.getAllMaintenanceRecords();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public ResponseEntity<ResourceMaintenance> getMaintenanceById(@PathVariable Long id) {
        Optional<ResourceMaintenance> maintenance = maintenanceService.getMaintenanceById(id);
        return maintenance.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public List<ResourceMaintenance> getMaintenanceByResourceId(@PathVariable Long resourceId) {
        return maintenanceService.getMaintenanceByResourceId(resourceId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> scheduleMaintenance(@RequestBody ResourceMaintenance maintenance) {
        try {
            ResourceMaintenance scheduledMaintenance = maintenanceService.scheduleMaintenance(maintenance);
            return ResponseEntity.ok(scheduledMaintenance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourceMaintenance> updateMaintenance(@PathVariable Long id, @RequestBody ResourceMaintenance maintenanceDetails) {
        try {
            ResourceMaintenance updatedMaintenance = maintenanceService.updateMaintenance(id, maintenanceDetails);
            return ResponseEntity.ok(updatedMaintenance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public ResponseEntity<?> startMaintenance(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String technicianId = request.get("technicianId");
            String technicianName = request.get("technicianName");
            
            ResourceMaintenance maintenance = maintenanceService.startMaintenance(id, technicianId, technicianName);
            return ResponseEntity.ok(maintenance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public ResponseEntity<?> completeMaintenance(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String notes = request.get("notes") != null ? request.get("notes").toString() : null;
            Double cost = request.get("cost") != null ? Double.valueOf(request.get("cost").toString()) : null;
            
            ResourceMaintenance maintenance = maintenanceService.completeMaintenance(id, notes, cost);
            return ResponseEntity.ok(maintenance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> cancelMaintenance(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            
            ResourceMaintenance maintenance = maintenanceService.cancelMaintenance(id, reason);
            return ResponseEntity.ok(maintenance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        try {
            maintenanceService.deleteMaintenance(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public List<ResourceMaintenance> getMaintenanceByStatus(@PathVariable ResourceMaintenance.MaintenanceStatus status) {
        return maintenanceService.getMaintenanceByStatus(status);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceMaintenance> getOverdueMaintenance() {
        return maintenanceService.getOverdueMaintenance();
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public List<ResourceMaintenance> getMaintenanceInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return maintenanceService.getMaintenanceInDateRange(startDate, endDate);
    }

    @GetMapping("/technician/{technicianId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public List<ResourceMaintenance> getMaintenanceByTechnician(@PathVariable String technicianId) {
        return maintenanceService.getMaintenanceByTechnician(technicianId);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public List<ResourceMaintenance> getMaintenanceByType(@PathVariable ResourceMaintenance.MaintenanceType type) {
        return maintenanceService.getMaintenanceByType(type);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR', 'TECHNICIAN')")
    public List<ResourceMaintenance> getActiveMaintenance() {
        return maintenanceService.getActiveMaintenance();
    }

    @GetMapping("/completed-since")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Long> countCompletedMaintenanceSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        Long count = maintenanceService.countCompletedMaintenanceSince(date);
        return ResponseEntity.ok(count != null ? count : 0L);
    }

    @GetMapping("/with-cost")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceMaintenance> getMaintenanceWithCost() {
        return maintenanceService.getMaintenanceWithCost();
    }

    @GetMapping("/total-cost")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Double> calculateTotalMaintenanceCost(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double totalCost = maintenanceService.calculateTotalMaintenanceCost(startDate, endDate);
        return ResponseEntity.ok(totalCost != null ? totalCost : 0.0);
    }

    @PostMapping("/check-overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> checkAndUpdateOverdueMaintenance() {
        maintenanceService.checkAndUpdateOverdueMaintenance();
        return ResponseEntity.ok("Overdue maintenance checked and updated successfully");
    }

    @PostMapping("/emergency")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createEmergencyMaintenance(@RequestBody Map<String, Object> request) {
        try {
            Long resourceId = Long.valueOf(request.get("resourceId").toString());
            String description = request.get("description").toString();
            String technicianId = request.get("technicianId") != null ? request.get("technicianId").toString() : null;
            String technicianName = request.get("technicianName") != null ? request.get("technicianName").toString() : null;
            
            ResourceMaintenance emergencyMaintenance = maintenanceService.createEmergencyMaintenance(
                resourceId, description, technicianId, technicianName);
            
            return ResponseEntity.ok(emergencyMaintenance);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Object> getMaintenanceStatistics() {
        List<ResourceMaintenance> overdueMaintenance = maintenanceService.getOverdueMaintenance();
        List<ResourceMaintenance> activeMaintenance = maintenanceService.getActiveMaintenance();
        Long completedCount = maintenanceService.countCompletedMaintenanceSince(
            LocalDateTime.now().minusMonths(1));
        
        return ResponseEntity.ok(Map.of(
            "overdue", overdueMaintenance.size(),
            "active", activeMaintenance.size(),
            "completedThisMonth", completedCount != null ? completedCount : 0L
        ));
    }
}
