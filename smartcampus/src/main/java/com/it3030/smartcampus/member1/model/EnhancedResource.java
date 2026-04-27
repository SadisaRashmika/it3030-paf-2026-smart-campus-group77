package com.it3030.smartcampus.member1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "enhanced_resources")
public class EnhancedResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ResourceCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private ResourceLocation location;

    @Column(nullable = false)
    private String type;

    @Column(name = "resource_code", unique = true, nullable = false)
    private String resourceCode;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean available = true;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Column(name = "current_capacity")
    private Integer currentCapacity = 0;

    @Column(name = "equipment_list", columnDefinition = "TEXT")
    private String equipmentList;

    @Column(name = "usage_rules", columnDefinition = "TEXT")
    private String usageRules;

    @Column(name = "maintenance_status")
    @Enumerated(EnumType.STRING)
    private MaintenanceStatus maintenanceStatus = MaintenanceStatus.GOOD;

    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDateTime nextMaintenanceDate;

    @Column(name = "average_rating", columnDefinition = "DECIMAL(3,2) DEFAULT 0.00")
    private Double averageRating = 0.0;

    @Column(name = "total_ratings")
    private Integer totalRatings = 0;

    @Column(name = "usage_count", columnDefinition = "INT DEFAULT 0")
    private Integer usageCount = 0;

    @Column(name = "booking_count", columnDefinition = "INT DEFAULT 0")
    private Integer bookingCount = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<ResourceRating> ratings;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<ResourceMaintenance> maintenanceRecords;

    public enum MaintenanceStatus {
        GOOD, NEEDS_ATTENTION, UNDER_MAINTENANCE, OUT_OF_ORDER
    }

    public EnhancedResource() {}

    public EnhancedResource(String name, String description, ResourceCategory category, 
                           ResourceLocation location, String type, String resourceCode) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.location = location;
        this.type = type;
        this.resourceCode = resourceCode;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ResourceCategory getCategory() { return category; }
    public void setCategory(ResourceCategory category) { this.category = category; }

    public ResourceLocation getLocation() { return location; }
    public void setLocation(ResourceLocation location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getResourceCode() { return resourceCode; }
    public void setResourceCode(String resourceCode) { this.resourceCode = resourceCode; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getCurrentCapacity() { return currentCapacity; }
    public void setCurrentCapacity(Integer currentCapacity) { this.currentCapacity = currentCapacity; }

    public String getEquipmentList() { return equipmentList; }
    public void setEquipmentList(String equipmentList) { this.equipmentList = equipmentList; }

    public String getUsageRules() { return usageRules; }
    public void setUsageRules(String usageRules) { this.usageRules = usageRules; }

    public MaintenanceStatus getMaintenanceStatus() { return maintenanceStatus; }
    public void setMaintenanceStatus(MaintenanceStatus maintenanceStatus) { this.maintenanceStatus = maintenanceStatus; }

    public LocalDateTime getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(LocalDateTime lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }

    public LocalDateTime getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(LocalDateTime nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getTotalRatings() { return totalRatings; }
    public void setTotalRatings(Integer totalRatings) { this.totalRatings = totalRatings; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public Integer getBookingCount() { return bookingCount; }
    public void setBookingCount(Integer bookingCount) { this.bookingCount = bookingCount; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<ResourceRating> getRatings() { return ratings; }
    public void setRatings(List<ResourceRating> ratings) { this.ratings = ratings; }

    public List<ResourceMaintenance> getMaintenanceRecords() { return maintenanceRecords; }
    public void setMaintenanceRecords(List<ResourceMaintenance> maintenanceRecords) { this.maintenanceRecords = maintenanceRecords; }
}
