package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourceInventory;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.repository.ResourceInventoryRepository;
import com.it3030.smartcampus.member1.repository.EnhancedResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceInventoryService {

    @Autowired
    private ResourceInventoryRepository inventoryRepository;

    @Autowired
    private EnhancedResourceRepository resourceRepository;

    public List<ResourceInventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Optional<ResourceInventory> getInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }

    public Optional<ResourceInventory> getInventoryByItemCode(String itemCode) {
        return inventoryRepository.findByItemCode(itemCode);
    }

    public List<ResourceInventory> getInventoryByResource(Long resourceId) {
        return inventoryRepository.findInventoryByResourceId(resourceId);
    }

    public ResourceInventory createInventory(ResourceInventory inventory) {
        // Validate resource exists
        EnhancedResource resource = resourceRepository.findById(inventory.getResource().getId())
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + inventory.getResource().getId()));

        // Check if item code already exists
        if (inventoryRepository.existsByItemCode(inventory.getItemCode())) {
            throw new IllegalArgumentException("Item code already exists: " + inventory.getItemCode());
        }

        // Validate stock levels
        if (inventory.getCurrentStock() < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative");
        }

        if (inventory.getMinimumStock() < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }

        if (inventory.getReorderLevel() < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative");
        }

        if (inventory.getMaximumStock() != null && inventory.getMaximumStock() < inventory.getCurrentStock()) {
            throw new IllegalArgumentException("Maximum stock cannot be less than current stock");
        }

        inventory.setResource(resource);
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.setLastRestockedDate(LocalDateTime.now());

        // Calculate total value
        inventory.calculateTotalValue();

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory updateInventory(Long id, ResourceInventory inventoryDetails) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        // Check if item code is being changed and if new code already exists
        if (!inventory.getItemCode().equals(inventoryDetails.getItemCode()) && 
            inventoryRepository.existsByItemCode(inventoryDetails.getItemCode())) {
            throw new IllegalArgumentException("Item code already exists: " + inventoryDetails.getItemCode());
        }

        // Validate stock levels
        if (inventoryDetails.getCurrentStock() < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative");
        }

        if (inventoryDetails.getMinimumStock() < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }

        if (inventoryDetails.getReorderLevel() < 0) {
            throw new IllegalArgumentException("Reorder level cannot be negative");
        }

        if (inventoryDetails.getMaximumStock() != null && inventoryDetails.getMaximumStock() < inventoryDetails.getCurrentStock()) {
            throw new IllegalArgumentException("Maximum stock cannot be less than current stock");
        }

        inventory.setItemName(inventoryDetails.getItemName());
        inventory.setItemCode(inventoryDetails.getItemCode());
        inventory.setDescription(inventoryDetails.getDescription());
        inventory.setCategory(inventoryDetails.getCategory());
        inventory.setUnitOfMeasure(inventoryDetails.getUnitOfMeasure());
        inventory.setCurrentStock(inventoryDetails.getCurrentStock());
        inventory.setMinimumStock(inventoryDetails.getMinimumStock());
        inventory.setMaximumStock(inventoryDetails.getMaximumStock());
        inventory.setReorderLevel(inventoryDetails.getReorderLevel());
        inventory.setUnitCost(inventoryDetails.getUnitCost());
        inventory.setSupplierName(inventoryDetails.getSupplierName());
        inventory.setSupplierContact(inventoryDetails.getSupplierContact());
        inventory.setStorageLocation(inventoryDetails.getStorageLocation());
        inventory.setConditionStatus(inventoryDetails.getConditionStatus());
        inventory.setWarrantyExpiry(inventoryDetails.getWarrantyExpiry());
        inventory.setMaintenanceRequired(inventoryDetails.getMaintenanceRequired());
        inventory.setExpiryDate(inventoryDetails.getExpiryDate());
        inventory.setUpdatedAt(LocalDateTime.now());

        // Recalculate total value
        inventory.calculateTotalValue();

        return inventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventoryRepository.delete(inventory);
    }

    public List<ResourceInventory> getItemsNeedingRestock() {
        return inventoryRepository.findItemsNeedingRestock();
    }

    public List<ResourceInventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    public List<ResourceInventory> getOverstockedItems() {
        return inventoryRepository.findOverstockedItems();
    }

    public List<ResourceInventory> getExpiringItems(LocalDateTime date) {
        return inventoryRepository.findExpiringItems(date);
    }

    public List<ResourceInventory> getWarrantyExpiringItems(LocalDateTime date) {
        return inventoryRepository.findWarrantyExpiringItems(date);
    }

    public List<ResourceInventory> getItemsNeedingMaintenance(LocalDateTime date) {
        return inventoryRepository.findItemsNeedingMaintenance(date);
    }

    public List<ResourceInventory> getInventoryByCategory(String category) {
        return inventoryRepository.findByCategory(category);
    }

    public List<ResourceInventory> getInventoryByConditionStatus(ResourceInventory.ConditionStatus conditionStatus) {
        return inventoryRepository.findByConditionStatus(conditionStatus);
    }

    public List<ResourceInventory> getInventoryBySupplier(String supplierName) {
        return inventoryRepository.findBySupplier(supplierName);
    }

    public List<ResourceInventory> getInventoryByStorageLocation(String storageLocation) {
        return inventoryRepository.findByStorageLocation(storageLocation);
    }

    public List<ResourceInventory> searchInventory(String searchTerm) {
        return inventoryRepository.searchItems(searchTerm);
    }

    public List<ResourceInventory> searchInventoryForResource(Long resourceId, String searchTerm) {
        return inventoryRepository.searchItemsForResource(resourceId, searchTerm);
    }

    public ResourceInventory restockItem(Long id, Integer quantity, Double unitCost) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }

        inventory.setCurrentStock(inventory.getCurrentStock() + quantity);
        inventory.setLastRestockedDate(LocalDateTime.now());
        
        if (unitCost != null) {
            inventory.setUnitCost(unitCost);
        }
        
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.calculateTotalValue();

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory consumeItem(Long id, Integer quantity) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        if (quantity <= 0) {
            throw new IllegalArgumentException("Consume quantity must be positive");
        }

        if (inventory.getCurrentStock() < quantity) {
            throw new IllegalStateException("Insufficient stock. Current: " + inventory.getCurrentStock() + ", Requested: " + quantity);
        }

        inventory.setCurrentStock(inventory.getCurrentStock() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.calculateTotalValue();

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory adjustStock(Long id, Integer newStock) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock level cannot be negative");
        }

        inventory.setCurrentStock(newStock);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventory.calculateTotalValue();

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory updateCondition(Long id, ResourceInventory.ConditionStatus conditionStatus) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setConditionStatus(conditionStatus);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory scheduleMaintenance(Long id, LocalDateTime maintenanceDate) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setMaintenanceRequired(true);
        inventory.setNextMaintenanceDate(maintenanceDate);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory completeMaintenance(Long id) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setMaintenanceRequired(false);
        inventory.setLastMaintenanceDate(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public Double calculateTotalInventoryValue() {
        return inventoryRepository.calculateTotalInventoryValue();
    }

    public Double calculateInventoryValueForResource(Long resourceId) {
        return inventoryRepository.calculateInventoryValueForResource(resourceId);
    }

    public long countItemsNeedingRestock() {
        return inventoryRepository.countItemsNeedingRestock();
    }

    public long countLowStockItems() {
        return inventoryRepository.countLowStockItems();
    }

    public long countExpiringItems(LocalDateTime date) {
        return inventoryRepository.countExpiringItems(date);
    }

    public List<String> getAllCategories() {
        return inventoryRepository.findAllCategories();
    }

    public List<String> getAllSuppliers() {
        return inventoryRepository.findAllSuppliers();
    }

    public List<String> getAllStorageLocations() {
        return inventoryRepository.findAllStorageLocations();
    }

    public List<ResourceInventory> getActiveInventoryForResource(Long resourceId) {
        return inventoryRepository.findActiveInventoryForResource(resourceId);
    }

    public List<ResourceInventory> getAvailableInventoryForResource(Long resourceId) {
        return inventoryRepository.findAvailableInventoryForResource(resourceId);
    }

    public List<ResourceInventory> getMaintenanceRequiredItemsForResource(Long resourceId) {
        return inventoryRepository.findMaintenanceRequiredItemsForResource(resourceId);
    }

    public List<ResourceInventory> getItemsByStockLevelAsc() {
        return inventoryRepository.findItemsByStockLevelAsc();
    }

    public List<ResourceInventory> getItemsByStockLevelDesc() {
        return inventoryRepository.findItemsByStockLevelDesc();
    }

    public List<ResourceInventory> getItemsByValueDesc() {
        return inventoryRepository.findItemsByValueDesc();
    }

    public List<ResourceInventory> getItemsByCostRange(Double minCost, Double maxCost) {
        return inventoryRepository.findByCostRange(minCost, maxCost);
    }

    public List<ResourceInventory> getItemsByStockRange(Integer minStock, Integer maxStock) {
        return inventoryRepository.findByStockRange(minStock, maxStock);
    }

    public List<ResourceInventory> getRecentlyRestockedItems(LocalDateTime date) {
        return inventoryRepository.findRecentlyRestockedItems(date);
    }

    public List<ResourceInventory> getScheduledRestocks(LocalDateTime date) {
        return inventoryRepository.findScheduledRestocks(date);
    }

    public long countItemsForResource(Long resourceId) {
        return inventoryRepository.countItemsForResource(resourceId);
    }

    public List<ResourceInventory> getItemsByConditionForResource(Long resourceId, ResourceInventory.ConditionStatus conditionStatus) {
        return inventoryRepository.findItemsByConditionForResource(resourceId, conditionStatus);
    }

    public List<ResourceInventory> getItemsByCategoryForResource(Long resourceId, String category) {
        return inventoryRepository.findItemsByCategoryForResource(resourceId, category);
    }

    public List<ResourceInventory> getRestockHistoryForResource(Long resourceId, LocalDateTime startDate, LocalDateTime endDate) {
        return inventoryRepository.findRestockHistoryForResource(resourceId, startDate, endDate);
    }

    public ResourceInventory updateSupplierInfo(Long id, String supplierName, String supplierContact) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setSupplierName(supplierName);
        inventory.setSupplierContact(supplierContact);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory updateStorageLocation(Long id, String storageLocation) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setStorageLocation(storageLocation);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory setExpiryDate(Long id, LocalDateTime expiryDate) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setExpiryDate(expiryDate);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory setWarrantyExpiry(Long id, LocalDateTime warrantyExpiry) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setWarrantyExpiry(warrantyExpiry);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public void processExpiringItems() {
        LocalDateTime thirtyDaysFromNow = LocalDateTime.now().plusDays(30);
        List<ResourceInventory> expiringItems = getExpiringItems(thirtyDaysFromNow);
        
        // Here you could trigger notifications or other actions for expiring items
        for (ResourceInventory item : expiringItems) {
            // Mark as needing attention if expiring within 7 days
            if (item.getExpiryDate().isBefore(LocalDateTime.now().plusDays(7))) {
                item.setMaintenanceRequired(true);
                inventoryRepository.save(item);
            }
        }
    }

    public void processWarrantyExpiringItems() {
        LocalDateTime thirtyDaysFromNow = LocalDateTime.now().plusDays(30);
        List<ResourceInventory> warrantyExpiringItems = getWarrantyExpiringItems(thirtyDaysFromNow);
        
        // Here you could trigger notifications or other actions for warranty expiring items
        for (ResourceInventory item : warrantyExpiringItems) {
            // Mark as needing attention if warranty expiring within 7 days
            if (item.getWarrantyExpiry().isBefore(LocalDateTime.now().plusDays(7))) {
                item.setMaintenanceRequired(true);
                inventoryRepository.save(item);
            }
        }
    }

    public void processMaintenanceRequiredItems() {
        List<ResourceInventory> maintenanceItems = getItemsNeedingMaintenance(LocalDateTime.now());
        
        // Here you could trigger maintenance work orders or notifications
        for (ResourceInventory item : maintenanceItems) {
            // Update maintenance status
            item.setMaintenanceRequired(true);
            inventoryRepository.save(item);
        }
    }

    public ResourceInventory markAsObsolete(Long id) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setConditionStatus(ResourceInventory.ConditionStatus.OBSOLETE);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public ResourceInventory markAsDamaged(Long id) {
        ResourceInventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found with id: " + id));

        inventory.setConditionStatus(ResourceInventory.ConditionStatus.DAMAGED);
        inventory.setMaintenanceRequired(true);
        inventory.setUpdatedAt(LocalDateTime.now());

        return inventoryRepository.save(inventory);
    }

    public void seedDefaultInventory() {
        if (inventoryRepository.count() == 0) {
            // This would be populated with default inventory items
            // Implementation would depend on the existing resources
        }
    }
}
