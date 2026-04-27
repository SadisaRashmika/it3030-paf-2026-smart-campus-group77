package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.ResourceInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceInventoryRepository extends JpaRepository<ResourceInventory, Long> {

    @Query("SELECT i FROM ResourceInventory i WHERE i.resource.id = :resourceId")
    List<ResourceInventory> findInventoryByResourceId(@Param("resourceId") Long resourceId);
    
    Optional<ResourceInventory> findByItemCode(String itemCode);
    
    boolean existsByItemCode(String itemCode);
    
    List<ResourceInventory> findByCategory(String category);
    
    List<ResourceInventory> findByConditionStatus(ResourceInventory.ConditionStatus conditionStatus);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.currentStock <= i.reorderLevel")
    List<ResourceInventory> findItemsNeedingRestock();
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.currentStock <= i.minimumStock")
    List<ResourceInventory> findLowStockItems();
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.maximumStock IS NOT NULL AND i.currentStock > i.maximumStock")
    List<ResourceInventory> findOverstockedItems();
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.expiryDate IS NOT NULL AND i.expiryDate <= :date")
    List<ResourceInventory> findExpiringItems(@Param("date") LocalDateTime date);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.warrantyExpiry IS NOT NULL AND i.warrantyExpiry <= :date")
    List<ResourceInventory> findWarrantyExpiringItems(@Param("date") LocalDateTime date);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.maintenanceRequired = true OR " +
           "(i.nextMaintenanceDate IS NOT NULL AND i.nextMaintenanceDate <= :date)")
    List<ResourceInventory> findItemsNeedingMaintenance(@Param("date") LocalDateTime date);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.supplierName = :supplierName")
    List<ResourceInventory> findBySupplier(@Param("supplierName") String supplierName);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.storageLocation = :storageLocation")
    List<ResourceInventory> findByStorageLocation(@Param("storageLocation") String storageLocation);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.itemName LIKE %:name% OR i.description LIKE %:name%")
    List<ResourceInventory> findByNameOrDescriptionContaining(@Param("name") String name);
    
    @Query("SELECT COUNT(i) FROM ResourceInventory i WHERE i.currentStock <= i.reorderLevel")
    long countItemsNeedingRestock();
    
    @Query("SELECT COUNT(i) FROM ResourceInventory i WHERE i.currentStock <= i.minimumStock")
    long countLowStockItems();
    
    @Query("SELECT COUNT(i) FROM ResourceInventory i WHERE i.expiryDate IS NOT NULL AND i.expiryDate <= :date")
    long countExpiringItems(@Param("date") LocalDateTime date);
    
    @Query("SELECT SUM(i.currentStock * i.unitCost) FROM ResourceInventory i WHERE i.unitCost IS NOT NULL")
    Double calculateTotalInventoryValue();
    
    @Query("SELECT SUM(i.currentStock * i.unitCost) FROM ResourceInventory i WHERE i.resource.id = :resourceId AND i.unitCost IS NOT NULL")
    Double calculateInventoryValueForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.lastRestockedDate >= :date")
    List<ResourceInventory> findRecentlyRestockedItems(@Param("date") LocalDateTime date);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.nextRestockDate IS NOT NULL AND i.nextRestockDate <= :date")
    List<ResourceInventory> findScheduledRestocks(@Param("date") LocalDateTime date);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.unitCost BETWEEN :minCost AND :maxCost")
    List<ResourceInventory> findByCostRange(@Param("minCost") Double minCost, @Param("maxCost") Double maxCost);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.currentStock BETWEEN :minStock AND :maxStock")
    List<ResourceInventory> findByStockRange(@Param("minStock") Integer minStock, @Param("maxStock") Integer maxStock);
    
    @Query("SELECT DISTINCT i.category FROM ResourceInventory i ORDER BY i.category")
    List<String> findAllCategories();
    
    @Query("SELECT DISTINCT i.supplierName FROM ResourceInventory i WHERE i.supplierName IS NOT NULL ORDER BY i.supplierName")
    List<String> findAllSuppliers();
    
    @Query("SELECT DISTINCT i.storageLocation FROM ResourceInventory i WHERE i.storageLocation IS NOT NULL ORDER BY i.storageLocation")
    List<String> findAllStorageLocations();
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.resource.id = :resourceId AND i.conditionStatus != 'OBSOLETE'")
    List<ResourceInventory> findActiveInventoryForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.resource.id = :resourceId AND i.currentStock > 0")
    List<ResourceInventory> findAvailableInventoryForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.resource.id = :resourceId AND i.maintenanceRequired = true")
    List<ResourceInventory> findMaintenanceRequiredItemsForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT i FROM ResourceInventory i ORDER BY i.currentStock ASC")
    List<ResourceInventory> findItemsByStockLevelAsc();
    
    @Query("SELECT i FROM ResourceInventory i ORDER BY i.currentStock DESC")
    List<ResourceInventory> findItemsByStockLevelDesc();
    
    @Query("SELECT i FROM ResourceInventory i ORDER BY i.totalValue DESC")
    List<ResourceInventory> findItemsByValueDesc();
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.itemName LIKE %:searchTerm% OR " +
           "i.itemCode LIKE %:searchTerm% OR i.description LIKE %:searchTerm% OR " +
           "i.category LIKE %:searchTerm% OR i.supplierName LIKE %:searchTerm%")
    List<ResourceInventory> searchItems(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.resource.id = :resourceId AND " +
           "(i.itemName LIKE %:searchTerm% OR i.itemCode LIKE %:searchTerm% OR i.description LIKE %:searchTerm%)")
    List<ResourceInventory> searchItemsForResource(@Param("resourceId") Long resourceId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT COUNT(i) FROM ResourceInventory i WHERE i.resource.id = :resourceId")
    long countItemsForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.resource.id = :resourceId AND i.conditionStatus = :conditionStatus")
    List<ResourceInventory> findItemsByConditionForResource(@Param("resourceId") Long resourceId, @Param("conditionStatus") ResourceInventory.ConditionStatus conditionStatus);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.resource.id = :resourceId AND i.category = :category")
    List<ResourceInventory> findItemsByCategoryForResource(@Param("resourceId") Long resourceId, @Param("category") String category);
    
    @Query("SELECT i FROM ResourceInventory i WHERE i.resource.id = :resourceId AND " +
           "i.lastRestockedDate >= :startDate AND i.lastRestockedDate <= :endDate")
    List<ResourceInventory> findRestockHistoryForResource(@Param("resourceId") Long resourceId, 
                                                         @Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
}
