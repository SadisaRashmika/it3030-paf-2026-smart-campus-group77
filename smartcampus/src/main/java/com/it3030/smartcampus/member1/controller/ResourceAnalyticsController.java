package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourceAnalytics;
import com.it3030.smartcampus.member1.service.ResourceAnalyticsService;
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
@RequestMapping("/api/member1/analytics")
public class ResourceAnalyticsController {

    @Autowired
    private ResourceAnalyticsService analyticsService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceAnalytics> getAllAnalytics() {
        return analyticsService.getAllAnalytics();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourceAnalytics> getAnalyticsById(@PathVariable Long id) {
        Optional<ResourceAnalytics> analytics = analyticsService.getAnalyticsById(id);
        return analytics.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceAnalytics> getAnalyticsByResourceId(@PathVariable Long resourceId) {
        return analyticsService.getAnalyticsByResourceId(resourceId);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceAnalytics> getAnalyticsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return analyticsService.getAnalyticsInDateRange(startDate, endDate);
    }

    @GetMapping("/resource/{resourceId}/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceAnalytics> getResourceAnalyticsInDateRange(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return analyticsService.getResourceAnalyticsInDateRange(resourceId, startDate, endDate);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResourceAnalytics createAnalytics(@RequestBody ResourceAnalytics analytics) {
        return analyticsService.createOrUpdateAnalytics(analytics);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourceAnalytics> updateAnalytics(@PathVariable Long id, @RequestBody ResourceAnalytics analyticsDetails) {
        try {
            ResourceAnalytics updatedAnalytics = analyticsService.updateAnalytics(id, analyticsDetails);
            return ResponseEntity.ok(updatedAnalytics);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Void> deleteAnalytics(@PathVariable Long id) {
        try {
            analyticsService.deleteAnalytics(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/resource/{resourceId}/average-bookings")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Double> getAverageBookingsForResource(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sinceDate) {
        Double average = analyticsService.getAverageBookingsForResource(resourceId, sinceDate);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @GetMapping("/average-utilization")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Double> getAverageUtilizationRate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime sinceDate) {
        Double average = analyticsService.getAverageUtilizationRate(sinceDate);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    @GetMapping("/high-utilization/{minRate}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceAnalytics> getHighUtilizationResources(@PathVariable Double minRate) {
        return analyticsService.getHighUtilizationResources(minRate);
    }

    @GetMapping("/high-satisfaction/{minScore}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceAnalytics> getHighSatisfactionResources(@PathVariable Double minScore) {
        return analyticsService.getHighSatisfactionResources(minScore);
    }

    @GetMapping("/total-revenue")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Double> calculateTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double totalRevenue = analyticsService.calculateTotalRevenue(startDate, endDate);
        return ResponseEntity.ok(totalRevenue != null ? totalRevenue : 0.0);
    }

    @GetMapping("/most-booked")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceAnalytics> getMostBookedResources() {
        return analyticsService.getMostBookedResources();
    }

    @GetMapping("/highest-rated")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceAnalytics> getHighestRatedResources() {
        return analyticsService.getHighestRatedResources();
    }

    @GetMapping("/resource/{resourceId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Map<String, Object>> getResourceStatistics(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, Object> statistics = analyticsService.getResourceStatistics(resourceId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/overall-statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Map<String, Object>> getOverallStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, Object> statistics = analyticsService.getOverallStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/generate-daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> generateDailyAnalytics() {
        analyticsService.generateDailyAnalytics();
        return ResponseEntity.ok("Daily analytics generated successfully");
    }

    @PostMapping("/generate-monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> generateMonthlyAnalytics() {
        analyticsService.generateMonthlyAnalytics();
        return ResponseEntity.ok("Monthly analytics generated successfully");
    }

    @DeleteMapping("/cleanup")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> deleteOldAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cutoffDate) {
        analyticsService.deleteOldAnalytics(cutoffDate);
        return ResponseEntity.ok("Old analytics deleted successfully");
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Map<String, Object>> getDashboardAnalytics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.minusMonths(1);
        LocalDateTime weekStart = now.minusWeeks(1);
        
        Map<String, Object> monthlyStats = analyticsService.getOverallStatistics(monthStart, now);
        Map<String, Object> weeklyStats = analyticsService.getOverallStatistics(weekStart, now);
        
        List<ResourceAnalytics> mostBooked = analyticsService.getMostBookedResources();
        List<ResourceAnalytics> highestRated = analyticsService.getHighestRatedResources();
        List<ResourceAnalytics> highUtilization = analyticsService.getHighUtilizationResources(80.0);
        
        return ResponseEntity.ok(Map.of(
            "monthlyStatistics", monthlyStats,
            "weeklyStatistics", weeklyStats,
            "mostBookedResources", mostBooked.stream().limit(5).toList(),
            "highestRatedResources", highestRated.stream().limit(5).toList(),
            "highUtilizationResources", highUtilization.stream().limit(5).toList()
        ));
    }
}
