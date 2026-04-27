package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourceInventory;
import com.it3030.smartcampus.member1.service.ResourceInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/inventory")
public class ResourceInventoryController {

    @Autowired
    private ResourceInventoryService inventoryService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceInventory> getInventoryById(@PathVariable Long id) {
        Optional<ResourceInventory> inventory = inventoryService.getInventoryById(id);
        return inventory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{itemCode}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceInventory> getInventoryByItemCode(@PathVariable String itemCode) {
        Optional<ResourceInventory> inventory = inventoryService.getInventoryByItemCode(itemCode);
        return inventory.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getInventoryByResource(@PathVariable Long resourceId) {
        return inventoryService.getInventoryByResource(resourceId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createInventory(@RequestBody ResourceInventory inventory) {
        try {
            ResourceInventory createdInventory = inventoryService.createInventory(inventory);
            return ResponseEntity.ok(createdInventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> updateInventory(@PathVariable Long id, @RequestBody ResourceInventory inventoryDetails) {
        try {
            ResourceInventory updatedInventory = inventoryService.updateInventory(id, inventoryDetails);
            return ResponseEntity.ok(updatedInventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        try {
            inventoryService.deleteInventory(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/needs-restock")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getItemsNeedingRestock() {
        return inventoryService.getItemsNeedingRestock();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    @GetMapping("/overstocked")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getOverstockedItems() {
        return inventoryService.getOverstockedItems();
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getExpiringItems(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().plusDays(30)}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return inventoryService.getExpiringItems(date);
    }

    @GetMapping("/warranty-expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getWarrantyExpiringItems(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().plusDays(30)}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return inventoryService.getWarrantyExpiringItems(date);
    }

    @GetMapping("/maintenance-needed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getItemsNeedingMaintenance(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return inventoryService.getItemsNeedingMaintenance(date);
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getInventoryByCategory(@PathVariable String category) {
        return inventoryService.getInventoryByCategory(category);
    }

    @GetMapping("/condition/{conditionStatus}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getInventoryByCondition(@PathVariable ResourceInventory.ConditionStatus conditionStatus) {
        return inventoryService.getInventoryByConditionStatus(conditionStatus);
    }

    @GetMapping("/supplier/{supplierName}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getInventoryBySupplier(@PathVariable String supplierName) {
        return inventoryService.getInventoryBySupplier(supplierName);
    }

    @GetMapping("/location/{storageLocation}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getInventoryByLocation(@PathVariable String storageLocation) {
        return inventoryService.getInventoryByStorageLocation(storageLocation);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> searchInventory(@RequestParam String searchTerm) {
        return inventoryService.searchInventory(searchTerm);
    }

    @GetMapping("/search/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> searchInventoryForResource(@PathVariable Long resourceId, @RequestParam String searchTerm) {
        return inventoryService.searchInventoryForResource(resourceId, searchTerm);
    }

    @PatchMapping("/{id}/restock")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> restockItem(@PathVariable Long id, @RequestBody java.util.Map<String, Object> request) {
        try {
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            Double unitCost = request.get("unitCost") != null ? Double.valueOf(request.get("unitCost").toString()) : null;
            
            ResourceInventory inventory = inventoryService.restockItem(id, quantity, unitCost);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/consume")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> consumeItem(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> request) {
        try {
            Integer quantity = request.get("quantity");
            ResourceInventory inventory = inventoryService.consumeItem(id, quantity);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/adjust-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> adjustStock(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> request) {
        try {
            Integer newStock = request.get("newStock");
            ResourceInventory inventory = inventoryService.adjustStock(id, newStock);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/condition")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> updateCondition(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        try {
            ResourceInventory.ConditionStatus conditionStatus = ResourceInventory.ConditionStatus.valueOf(request.get("conditionStatus"));
            ResourceInventory inventory = inventoryService.updateCondition(id, conditionStatus);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/schedule-maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> scheduleMaintenance(@PathVariable Long id, @RequestBody java.util.Map<String, Object> request) {
        try {
            LocalDateTime maintenanceDate = LocalDateTime.parse(request.get("maintenanceDate").toString());
            ResourceInventory inventory = inventoryService.scheduleMaintenance(id, maintenanceDate);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/complete-maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> completeMaintenance(@PathVariable Long id) {
        try {
            ResourceInventory inventory = inventoryService.completeMaintenance(id);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/total-value")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Double> calculateTotalInventoryValue() {
        Double totalValue = inventoryService.calculateTotalInventoryValue();
        return ResponseEntity.ok(totalValue != null ? totalValue : 0.0);
    }

    @GetMapping("/value/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Double> calculateInventoryValueForResource(@PathVariable Long resourceId) {
        Double totalValue = inventoryService.calculateInventoryValueForResource(resourceId);
        return ResponseEntity.ok(totalValue != null ? totalValue : 0.0);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Object> getInventoryStatistics() {
        long needsRestockCount = inventoryService.countItemsNeedingRestock();
        long lowStockCount = inventoryService.countLowStockItems();
        long expiringCount = inventoryService.countExpiringItems(LocalDateTime.now().plusDays(30));
        
        return ResponseEntity.ok(java.util.Map.of(
            "needsRestock", needsRestockCount,
            "lowStock", lowStockCount,
            "expiringSoon", expiringCount
        ));
    }

    @GetMapping("/categories")
    @PreAuthorize("isAuthenticated()")
    public List<String> getAllCategories() {
        return inventoryService.getAllCategories();
    }

    @GetMapping("/suppliers")
    @PreAuthorize("isAuthenticated()")
    public List<String> getAllSuppliers() {
        return inventoryService.getAllSuppliers();
    }

    @GetMapping("/locations")
    @PreAuthorize("isAuthenticated()")
    public List<String> getAllStorageLocations() {
        return inventoryService.getAllStorageLocations();
    }

    @GetMapping("/active/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getActiveInventoryForResource(@PathVariable Long resourceId) {
        return inventoryService.getActiveInventoryForResource(resourceId);
    }

    @GetMapping("/available/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getAvailableInventoryForResource(@PathVariable Long resourceId) {
        return inventoryService.getAvailableInventoryForResource(resourceId);
    }

    @GetMapping("/maintenance/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getMaintenanceRequiredItemsForResource(@PathVariable Long resourceId) {
        return inventoryService.getMaintenanceRequiredItemsForResource(resourceId);
    }

    @GetMapping("/by-stock/asc")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getItemsByStockLevelAsc() {
        return inventoryService.getItemsByStockLevelAsc();
    }

    @GetMapping("/by-stock/desc")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getItemsByStockLevelDesc() {
        return inventoryService.getItemsByStockLevelDesc();
    }

    @GetMapping("/by-value/desc")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getItemsByValueDesc() {
        return inventoryService.getItemsByValueDesc();
    }

    @GetMapping("/cost-range")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getItemsByCostRange(@RequestParam Double minCost, @RequestParam Double maxCost) {
        return inventoryService.getItemsByCostRange(minCost, maxCost);
    }

    @GetMapping("/stock-range")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getItemsByStockRange(@RequestParam Integer minStock, @RequestParam Integer maxStock) {
        return inventoryService.getItemsByStockRange(minStock, maxStock);
    }

    @GetMapping("/recently-restocked")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getRecentlyRestockedItems(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().minusDays(7)}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return inventoryService.getRecentlyRestockedItems(date);
    }

    @GetMapping("/scheduled-restocks")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getScheduledRestocks(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now().plusDays(7)}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return inventoryService.getScheduledRestocks(date);
    }

    @GetMapping("/count/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countItemsForResource(@PathVariable Long resourceId) {
        long count = inventoryService.countItemsForResource(resourceId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/condition/{resourceId}/{conditionStatus}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getItemsByConditionForResource(@PathVariable Long resourceId, @PathVariable ResourceInventory.ConditionStatus conditionStatus) {
        return inventoryService.getItemsByConditionForResource(resourceId, conditionStatus);
    }

    @GetMapping("/category/{resourceId}/{category}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceInventory> getItemsByCategoryForResource(@PathVariable Long resourceId, @PathVariable String category) {
        return inventoryService.getItemsByCategoryForResource(resourceId, category);
    }

    @GetMapping("/restock-history/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceInventory> getRestockHistoryForResource(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return inventoryService.getRestockHistoryForResource(resourceId, startDate, endDate);
    }

    @PatchMapping("/{id}/supplier-info")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> updateSupplierInfo(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        try {
            String supplierName = request.get("supplierName");
            String supplierContact = request.get("supplierContact");
            
            ResourceInventory inventory = inventoryService.updateSupplierInfo(id, supplierName, supplierContact);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/storage-location")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> updateStorageLocation(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        try {
            String storageLocation = request.get("storageLocation");
            ResourceInventory inventory = inventoryService.updateStorageLocation(id, storageLocation);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/expiry-date")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> setExpiryDate(@PathVariable Long id, @RequestBody java.util.Map<String, Object> request) {
        try {
            LocalDateTime expiryDate = request.get("expiryDate") != null ? 
                LocalDateTime.parse(request.get("expiryDate").toString()) : null;
            ResourceInventory inventory = inventoryService.setExpiryDate(id, expiryDate);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/warranty-expiry")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> setWarrantyExpiry(@PathVariable Long id, @RequestBody java.util.Map<String, Object> request) {
        try {
            LocalDateTime warrantyExpiry = request.get("warrantyExpiry") != null ? 
                LocalDateTime.parse(request.get("warrantyExpiry").toString()) : null;
            ResourceInventory inventory = inventoryService.setWarrantyExpiry(id, warrantyExpiry);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/mark-obsolete")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> markAsObsolete(@PathVariable Long id) {
        try {
            ResourceInventory inventory = inventoryService.markAsObsolete(id);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/mark-damaged")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> markAsDamaged(@PathVariable Long id) {
        try {
            ResourceInventory inventory = inventoryService.markAsDamaged(id);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/process-expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> processExpiringItems() {
        inventoryService.processExpiringItems();
        return ResponseEntity.ok("Expiring items processed successfully");
    }

    @PostMapping("/process-warranty-expiring")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> processWarrantyExpiringItems() {
        inventoryService.processWarrantyExpiringItems();
        return ResponseEntity.ok("Warranty expiring items processed successfully");
    }

    @PostMapping("/process-maintenance")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> processMaintenanceRequiredItems() {
        inventoryService.processMaintenanceRequiredItems();
        return ResponseEntity.ok("Maintenance required items processed successfully");
    }

    @PostMapping("/seed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> seedDefaultInventory() {
        inventoryService.seedDefaultInventory();
        return ResponseEntity.ok("Default inventory seeded successfully");
    }
}
