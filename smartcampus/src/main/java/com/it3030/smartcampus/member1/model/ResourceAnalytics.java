package com.it3030.smartcampus.member1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_analytics")
public class ResourceAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private EnhancedResource resource;

    @Column(name = "date_recorded", nullable = false)
    private LocalDateTime dateRecorded;

    @Column(name = "total_bookings", nullable = false)
    private Integer totalBookings = 0;

    @Column(name = "total_usage_hours")
    private Double totalUsageHours = 0.0;

    @Column(name = "peak_usage_hour")
    private Integer peakUsageHour;

    @Column(name = "average_booking_duration")
    private Double averageBookingDuration = 0.0;

    @Column(name = "cancellation_count")
    private Integer cancellationCount = 0;

    @Column(name = "revenue_generated")
    private Double revenueGenerated = 0.0;

    @Column(name = "maintenance_downtime_hours")
    private Double maintenanceDowntimeHours = 0.0;

    @Column(name = "user_satisfaction_score")
    private Double userSatisfactionScore = 0.0;

    @Column(name = "utilization_rate")
    private Double utilizationRate = 0.0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ResourceAnalytics() {}

    public ResourceAnalytics(EnhancedResource resource, LocalDateTime dateRecorded) {
        this.resource = resource;
        this.dateRecorded = dateRecorded;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EnhancedResource getResource() { return resource; }
    public void setResource(EnhancedResource resource) { this.resource = resource; }

    public LocalDateTime getDateRecorded() { return dateRecorded; }
    public void setDateRecorded(LocalDateTime dateRecorded) { this.dateRecorded = dateRecorded; }

    public Integer getTotalBookings() { return totalBookings; }
    public void setTotalBookings(Integer totalBookings) { this.totalBookings = totalBookings; }

    public Double getTotalUsageHours() { return totalUsageHours; }
    public void setTotalUsageHours(Double totalUsageHours) { this.totalUsageHours = totalUsageHours; }

    public Integer getPeakUsageHour() { return peakUsageHour; }
    public void setPeakUsageHour(Integer peakUsageHour) { this.peakUsageHour = peakUsageHour; }

    public Double getAverageBookingDuration() { return averageBookingDuration; }
    public void setAverageBookingDuration(Double averageBookingDuration) { this.averageBookingDuration = averageBookingDuration; }

    public Integer getCancellationCount() { return cancellationCount; }
    public void setCancellationCount(Integer cancellationCount) { this.cancellationCount = cancellationCount; }

    public Double getRevenueGenerated() { return revenueGenerated; }
    public void setRevenueGenerated(Double revenueGenerated) { this.revenueGenerated = revenueGenerated; }

    public Double getMaintenanceDowntimeHours() { return maintenanceDowntimeHours; }
    public void setMaintenanceDowntimeHours(Double maintenanceDowntimeHours) { this.maintenanceDowntimeHours = maintenanceDowntimeHours; }

    public Double getUserSatisfactionScore() { return userSatisfactionScore; }
    public void setUserSatisfactionScore(Double userSatisfactionScore) { this.userSatisfactionScore = userSatisfactionScore; }

    public Double getUtilizationRate() { return utilizationRate; }
    public void setUtilizationRate(Double utilizationRate) { this.utilizationRate = utilizationRate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
