package com.it3030.smartcampus.member1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_inventory")
public class ResourceInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private EnhancedResource resource;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_code", unique = true, nullable = false)
    private String itemCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "unit_of_measure", nullable = false)
    private String unitOfMeasure;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock;

    @Column(name = "maximum_stock")
    private Integer maximumStock;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    @Column(name = "unit_cost")
    private Double unitCost;

    @Column(name = "total_value")
    private Double totalValue;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_contact")
    private String supplierContact;

    @Column(name = "last_restocked_date")
    private LocalDateTime lastRestockedDate;

    @Column(name = "next_restock_date")
    private LocalDateTime nextRestockDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "storage_location")
    private String storageLocation;

    @Column(name = "condition_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConditionStatus conditionStatus = ConditionStatus.NEW;

    @Column(name = "warranty_expiry")
    private LocalDateTime warrantyExpiry;

    @Column(name = "maintenance_required")
    private Boolean maintenanceRequired = false;

    @Column(name = "last_maintenance_date")
    private LocalDateTime lastMaintenanceDate;

    @Column(name = "next_maintenance_date")
    private LocalDateTime nextMaintenanceDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ConditionStatus {
        NEW, GOOD, FAIR, POOR, DAMAGED, OBSOLETE
    }

    public ResourceInventory() {}

    public ResourceInventory(EnhancedResource resource, String itemName, String itemCode,
                             String category, String unitOfMeasure, Integer currentStock,
                             Integer minimumStock, Integer reorderLevel) {
        this.resource = resource;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.category = category;
        this.unitOfMeasure = unitOfMeasure;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.reorderLevel = reorderLevel;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EnhancedResource getResource() { return resource; }
    public void setResource(EnhancedResource resource) { this.resource = resource; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer minimumStock) { this.minimumStock = minimumStock; }

    public Integer getMaximumStock() { return maximumStock; }
    public void setMaximumStock(Integer maximumStock) { this.maximumStock = maximumStock; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public Double getUnitCost() { return unitCost; }
    public void setUnitCost(Double unitCost) { this.unitCost = unitCost; }

    public Double getTotalValue() { return totalValue; }
    public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getSupplierContact() { return supplierContact; }
    public void setSupplierContact(String supplierContact) { this.supplierContact = supplierContact; }

    public LocalDateTime getLastRestockedDate() { return lastRestockedDate; }
    public void setLastRestockedDate(LocalDateTime lastRestockedDate) { this.lastRestockedDate = lastRestockedDate; }

    public LocalDateTime getNextRestockDate() { return nextRestockDate; }
    public void setNextRestockDate(LocalDateTime nextRestockDate) { this.nextRestockDate = nextRestockDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public ConditionStatus getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(ConditionStatus conditionStatus) { this.conditionStatus = conditionStatus; }

    public LocalDateTime getWarrantyExpiry() { return warrantyExpiry; }
    public void setWarrantyExpiry(LocalDateTime warrantyExpiry) { this.warrantyExpiry = warrantyExpiry; }

    public Boolean getMaintenanceRequired() { return maintenanceRequired; }
    public void setMaintenanceRequired(Boolean maintenanceRequired) { this.maintenanceRequired = maintenanceRequired; }

    public LocalDateTime getLastMaintenanceDate() { return lastMaintenanceDate; }
    public void setLastMaintenanceDate(LocalDateTime lastMaintenanceDate) { this.lastMaintenanceDate = lastMaintenanceDate; }

    public LocalDateTime getNextMaintenanceDate() { return nextMaintenanceDate; }
    public void setNextMaintenanceDate(LocalDateTime nextMaintenanceDate) { this.nextMaintenanceDate = nextMaintenanceDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business logic methods
    public boolean needsRestock() {
        return currentStock <= reorderLevel;
    }

    public boolean isLowStock() {
        return currentStock <= minimumStock;
    }

    public boolean isOverstocked() {
        return maximumStock != null && currentStock > maximumStock;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
    }

    public boolean warrantyExpired() {
        return warrantyExpiry != null && warrantyExpiry.isBefore(LocalDateTime.now());
    }

    public boolean needsMaintenance() {
        return maintenanceRequired || (nextMaintenanceDate != null && nextMaintenanceDate.isBefore(LocalDateTime.now()));
    }

    public void calculateTotalValue() {
        if (unitCost != null && currentStock != null) {
            totalValue = unitCost * currentStock;
        }
    }
}
