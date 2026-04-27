package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourceAnalytics;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.repository.ResourceAnalyticsRepository;
import com.it3030.smartcampus.member1.repository.EnhancedResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class ResourceAnalyticsService {

    @Autowired
    private ResourceAnalyticsRepository analyticsRepository;

    @Autowired
    private EnhancedResourceRepository resourceRepository;

    public List<ResourceAnalytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }

    public Optional<ResourceAnalytics> getAnalyticsById(Long id) {
        return analyticsRepository.findById(id);
    }

    public List<ResourceAnalytics> getAnalyticsByResourceId(Long resourceId) {
        return analyticsRepository.findByResourceId(resourceId);
    }

    public List<ResourceAnalytics> getAnalyticsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return analyticsRepository.findAnalyticsInDateRange(startDate, endDate);
    }

    public List<ResourceAnalytics> getResourceAnalyticsInDateRange(Long resourceId, LocalDateTime startDate, LocalDateTime endDate) {
        return analyticsRepository.findResourceAnalyticsInDateRange(resourceId, startDate, endDate);
    }

    public ResourceAnalytics createOrUpdateAnalytics(ResourceAnalytics analytics) {
        // Validate resource exists
        EnhancedResource resource = resourceRepository.findById(analytics.getResource().getId())
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + analytics.getResource().getId()));

        analytics.setResource(resource);
        analytics.setCreatedAt(LocalDateTime.now());

        return analyticsRepository.save(analytics);
    }

    public ResourceAnalytics updateAnalytics(Long id, ResourceAnalytics analyticsDetails) {
        ResourceAnalytics analytics = analyticsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analytics record not found with id: " + id));

        analytics.setTotalBookings(analyticsDetails.getTotalBookings());
        analytics.setTotalUsageHours(analyticsDetails.getTotalUsageHours());
        analytics.setPeakUsageHour(analyticsDetails.getPeakUsageHour());
        analytics.setAverageBookingDuration(analyticsDetails.getAverageBookingDuration());
        analytics.setCancellationCount(analyticsDetails.getCancellationCount());
        analytics.setRevenueGenerated(analyticsDetails.getRevenueGenerated());
        analytics.setMaintenanceDowntimeHours(analyticsDetails.getMaintenanceDowntimeHours());
        analytics.setUserSatisfactionScore(analyticsDetails.getUserSatisfactionScore());
        analytics.setUtilizationRate(analyticsDetails.getUtilizationRate());

        return analyticsRepository.save(analytics);
    }

    public Double getAverageBookingsForResource(Long resourceId, LocalDateTime sinceDate) {
        return analyticsRepository.findAverageBookingsForResource(resourceId, sinceDate);
    }

    public Double getAverageUtilizationRate(LocalDateTime sinceDate) {
        return analyticsRepository.findAverageUtilizationRate(sinceDate);
    }

    public List<ResourceAnalytics> getHighUtilizationResources(Double minUtilizationRate) {
        return analyticsRepository.findHighUtilizationResources(minUtilizationRate);
    }

    public List<ResourceAnalytics> getHighSatisfactionResources(Double minSatisfactionScore) {
        return analyticsRepository.findHighSatisfactionResources(minSatisfactionScore);
    }

    public Double calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return analyticsRepository.calculateTotalRevenue(startDate, endDate);
    }

    public List<ResourceAnalytics> getMostBookedResources() {
        return analyticsRepository.findMostBookedResources();
    }

    public List<ResourceAnalytics> getHighestRatedResources() {
        return analyticsRepository.findHighestRatedResources();
    }

    public void generateDailyAnalytics() {
        List<EnhancedResource> allResources = resourceRepository.findAll();
        LocalDateTime today = LocalDate.now().atStartOfDay();

        for (EnhancedResource resource : allResources) {
            // Check if analytics already exist for today
            List<ResourceAnalytics> existingAnalytics = analyticsRepository.findResourceAnalyticsInDateRange(
                resource.getId(), today, today.plusDays(1).minusSeconds(1));

            if (existingAnalytics.isEmpty()) {
                // Generate new analytics for today
                ResourceAnalytics analytics = new ResourceAnalytics(resource, today);
                
                // Calculate today's metrics (this would be based on actual booking data)
                // For now, we'll set default values
                analytics.setTotalBookings(0);
                analytics.setTotalUsageHours(0.0);
                analytics.setPeakUsageHour(null);
                analytics.setAverageBookingDuration(0.0);
                analytics.setCancellationCount(0);
                analytics.setRevenueGenerated(0.0);
                analytics.setMaintenanceDowntimeHours(0.0);
                analytics.setUserSatisfactionScore(0.0);
                analytics.setUtilizationRate(0.0);

                analyticsRepository.save(analytics);
            }
        }
    }

    public void generateMonthlyAnalytics() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

        List<EnhancedResource> allResources = resourceRepository.findAll();

        for (EnhancedResource resource : allResources) {
            // Aggregate daily analytics for the month
            List<ResourceAnalytics> monthlyData = analyticsRepository.findResourceAnalyticsInDateRange(
                resource.getId(), monthStart, monthEnd);

            if (!monthlyData.isEmpty()) {
                ResourceAnalytics monthlySummary = new ResourceAnalytics(resource, monthStart);
                
                // Aggregate metrics
                int totalBookings = monthlyData.stream().mapToInt(ResourceAnalytics::getTotalBookings).sum();
                double totalUsageHours = monthlyData.stream().mapToDouble(ResourceAnalytics::getTotalUsageHours).sum();
                double totalRevenue = monthlyData.stream().mapToDouble(ResourceAnalytics::getRevenueGenerated).sum();
                int totalCancellations = monthlyData.stream().mapToInt(ResourceAnalytics::getCancellationCount).sum();
                double totalDowntime = monthlyData.stream().mapToDouble(ResourceAnalytics::getMaintenanceDowntimeHours).sum();

                monthlySummary.setTotalBookings(totalBookings);
                monthlySummary.setTotalUsageHours(totalUsageHours);
                monthlySummary.setRevenueGenerated(totalRevenue);
                monthlySummary.setCancellationCount(totalCancellations);
                monthlySummary.setMaintenanceDowntimeHours(totalDowntime);

                // Calculate averages
                monthlySummary.setAverageBookingDuration(
                    monthlyData.stream().mapToDouble(ResourceAnalytics::getAverageBookingDuration).average().orElse(0.0));
                monthlySummary.setUserSatisfactionScore(
                    monthlyData.stream().mapToDouble(ResourceAnalytics::getUserSatisfactionScore).average().orElse(0.0));
                monthlySummary.setUtilizationRate(
                    monthlyData.stream().mapToDouble(ResourceAnalytics::getUtilizationRate).average().orElse(0.0));

                // Find peak usage hour
                monthlySummary.setPeakUsageHour(
                    monthlyData.stream()
                        .filter(a -> a.getPeakUsageHour() != null)
                        .mapToInt(ResourceAnalytics::getPeakUsageHour)
                        .max()
                        .orElse(0));

                analyticsRepository.save(monthlySummary);
            }
        }
    }

    public Map<String, Object> getResourceStatistics(Long resourceId, LocalDateTime startDate, LocalDateTime endDate) {
        List<ResourceAnalytics> analyticsData = analyticsRepository.findResourceAnalyticsInDateRange(resourceId, startDate, endDate);
        
        Map<String, Object> statistics = new HashMap<>();
        
        if (!analyticsData.isEmpty()) {
            statistics.put("totalBookings", analyticsData.stream().mapToInt(ResourceAnalytics::getTotalBookings).sum());
            statistics.put("totalUsageHours", analyticsData.stream().mapToDouble(ResourceAnalytics::getTotalUsageHours).sum());
            statistics.put("totalRevenue", analyticsData.stream().mapToDouble(ResourceAnalytics::getRevenueGenerated).sum());
            statistics.put("totalCancellations", analyticsData.stream().mapToInt(ResourceAnalytics::getCancellationCount).sum());
            statistics.put("averageBookingDuration", analyticsData.stream().mapToDouble(ResourceAnalytics::getAverageBookingDuration).average().orElse(0.0));
            statistics.put("averageSatisfactionScore", analyticsData.stream().mapToDouble(ResourceAnalytics::getUserSatisfactionScore).average().orElse(0.0));
            statistics.put("averageUtilizationRate", analyticsData.stream().mapToDouble(ResourceAnalytics::getUtilizationRate).average().orElse(0.0));
            statistics.put("peakUsageHour", analyticsData.stream()
                .filter(a -> a.getPeakUsageHour() != null)
                .mapToInt(ResourceAnalytics::getPeakUsageHour)
                .max()
                .orElse(0));
        } else {
            statistics.put("totalBookings", 0);
            statistics.put("totalUsageHours", 0.0);
            statistics.put("totalRevenue", 0.0);
            statistics.put("totalCancellations", 0);
            statistics.put("averageBookingDuration", 0.0);
            statistics.put("averageSatisfactionScore", 0.0);
            statistics.put("averageUtilizationRate", 0.0);
            statistics.put("peakUsageHour", null);
        }
        
        return statistics;
    }

    public Map<String, Object> getOverallStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<ResourceAnalytics> allAnalytics = analyticsRepository.findAnalyticsInDateRange(startDate, endDate);
        
        Map<String, Object> statistics = new HashMap<>();
        
        if (!allAnalytics.isEmpty()) {
            statistics.put("totalResources", allAnalytics.size());
            statistics.put("totalBookings", allAnalytics.stream().mapToInt(ResourceAnalytics::getTotalBookings).sum());
            statistics.put("totalUsageHours", allAnalytics.stream().mapToDouble(ResourceAnalytics::getTotalUsageHours).sum());
            statistics.put("totalRevenue", allAnalytics.stream().mapToDouble(ResourceAnalytics::getRevenueGenerated).sum());
            statistics.put("totalCancellations", allAnalytics.stream().mapToInt(ResourceAnalytics::getCancellationCount).sum());
            statistics.put("averageBookingDuration", allAnalytics.stream().mapToDouble(ResourceAnalytics::getAverageBookingDuration).average().orElse(0.0));
            statistics.put("averageSatisfactionScore", allAnalytics.stream().mapToDouble(ResourceAnalytics::getUserSatisfactionScore).average().orElse(0.0));
            statistics.put("averageUtilizationRate", allAnalytics.stream().mapToDouble(ResourceAnalytics::getUtilizationRate).average().orElse(0.0));
        } else {
            statistics.put("totalResources", 0);
            statistics.put("totalBookings", 0);
            statistics.put("totalUsageHours", 0.0);
            statistics.put("totalRevenue", 0.0);
            statistics.put("totalCancellations", 0);
            statistics.put("averageBookingDuration", 0.0);
            statistics.put("averageSatisfactionScore", 0.0);
            statistics.put("averageUtilizationRate", 0.0);
        }
        
        return statistics;
    }

    public void deleteAnalytics(Long id) {
        ResourceAnalytics analytics = analyticsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Analytics record not found with id: " + id));

        analyticsRepository.delete(analytics);
    }

    public void deleteOldAnalytics(LocalDateTime cutoffDate) {
        List<ResourceAnalytics> oldAnalytics = analyticsRepository.findAnalyticsInDateRange(
            LocalDateTime.of(1970, 1, 1, 0, 0), cutoffDate);
        
        analyticsRepository.deleteAll(oldAnalytics);
    }
}
