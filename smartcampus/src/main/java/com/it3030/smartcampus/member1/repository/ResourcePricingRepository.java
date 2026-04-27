package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.ResourcePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourcePricingRepository extends JpaRepository<ResourcePricing, Long> {

    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId")
    List<ResourcePricing> findPricingByResourceId(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND p.isActive = true")
    List<ResourcePricing> findActivePricingForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND p.isActive = true AND p.effectiveFrom <= CURRENT_TIMESTAMP AND (p.effectiveTo IS NULL OR p.effectiveTo >= CURRENT_TIMESTAMP)")
    Optional<ResourcePricing> findCurrentPricingForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.effectiveFrom <= :date AND (p.effectiveTo IS NULL OR p.effectiveTo >= :date) AND p.isActive = true")
    Optional<ResourcePricing> findEffectivePricingForResource(@Param("resourceId") Long resourceId, @Param("date") LocalDateTime date);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.pricingType = :pricingType")
    List<ResourcePricing> findByPricingType(@Param("pricingType") ResourcePricing.PricingType pricingType);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.currency = :currency")
    List<ResourcePricing> findByCurrency(@Param("currency") String currency);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.depositRequired = true")
    List<ResourcePricing> findPricingWithDeposit();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.discountAvailable = true")
    List<ResourcePricing> findPricingWithDiscount();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.peakHourPricing = true")
    List<ResourcePricing> findPricingWithPeakHourPricing();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.weekendPricing = true")
    List<ResourcePricing> findPricingWithWeekendPricing();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.holidayPricing = true")
    List<ResourcePricing> findPricingWithHolidayPricing();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.seasonalPricing = true")
    List<ResourcePricing> findPricingWithSeasonalPricing();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.bulkDiscountAvailable = true")
    List<ResourcePricing> findPricingWithBulkDiscount();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.memberDiscountAvailable = true")
    List<ResourcePricing> findPricingWithMemberDiscount();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.studentDiscountAvailable = true")
    List<ResourcePricing> findPricingWithStudentDiscount();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.basePrice BETWEEN :minPrice AND :maxPrice")
    List<ResourcePricing> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.cancellationPolicy = :policy")
    List<ResourcePricing> findByCancellationPolicy(@Param("policy") ResourcePricing.CancellationPolicy policy);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.effectiveFrom > :date")
    List<ResourcePricing> findFuturePricing(@Param("date") LocalDateTime date);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.effectiveTo IS NOT NULL AND p.effectiveTo < :date")
    List<ResourcePricing> findExpiredPricing(@Param("date") LocalDateTime date);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.effectiveFrom <= :date AND " +
           "(p.effectiveTo IS NULL OR p.effectiveTo >= :date) AND p.isActive = true")
    List<ResourcePricing> findCurrentlyEffectivePricing(@Param("date") LocalDateTime date);
    
    @Query("SELECT MIN(p.basePrice) FROM ResourcePricing p WHERE p.isActive = true")
    Double findMinimumActivePrice();
    
    @Query("SELECT MAX(p.basePrice) FROM ResourcePricing p WHERE p.isActive = true")
    Double findMaximumActivePrice();
    
    @Query("SELECT AVG(p.basePrice) FROM ResourcePricing p WHERE p.isActive = true")
    Double findAverageActivePrice();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.isActive = true ORDER BY p.effectiveFrom DESC")
    List<ResourcePricing> findActivePricingForResourceOrdered(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.pricingType = :pricingType AND p.isActive = true")
    Optional<ResourcePricing> findPricingByTypeForResource(@Param("resourceId") Long resourceId, 
                                                          @Param("pricingType") ResourcePricing.PricingType pricingType);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.basePrice <= :maxPrice AND p.isActive = true ORDER BY p.basePrice ASC")
    List<ResourcePricing> findAffordablePricingForResource(@Param("resourceId") Long resourceId, @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.minimumBookingHours <= :hours AND (p.maximumBookingHours IS NULL OR p.maximumBookingHours >= :hours) AND p.isActive = true")
    List<ResourcePricing> findPricingForBookingDuration(@Param("resourceId") Long resourceId, @Param("hours") Integer hours);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.cancellationPolicy = :policy AND p.isActive = true")
    Optional<ResourcePricing> findPricingByCancellationPolicyForResource(@Param("resourceId") Long resourceId, 
                                                                       @Param("policy") ResourcePricing.CancellationPolicy policy);
    
    @Query("SELECT COUNT(p) FROM ResourcePricing p WHERE p.isActive = true")
    long countActivePricing();
    
    @Query("SELECT COUNT(p) FROM ResourcePricing p WHERE p.resource.id = :resourceId AND p.isActive = true")
    long countActivePricingForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT COUNT(p) FROM ResourcePricing p WHERE p.discountAvailable = true AND p.isActive = true")
    long countPricingWithDiscount();
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.effectiveFrom <= :date AND (p.effectiveTo IS NULL OR p.effectiveTo >= :date) AND p.isActive = true")
    List<ResourcePricing> findEffectivePricingHistoryForResource(@Param("resourceId") Long resourceId, @Param("date") LocalDateTime date);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.seasonalPricing = true AND p.seasonalStartDate <= :date AND p.seasonalEndDate >= :date AND p.isActive = true")
    List<ResourcePricing> findSeasonalPricingForResource(@Param("resourceId") Long resourceId, @Param("date") LocalDateTime date);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.bulkDiscountAvailable = true AND p.bulkDiscountThreshold <= :quantity AND p.isActive = true")
    List<ResourcePricing> findBulkDiscountPricingForResource(@Param("resourceId") Long resourceId, @Param("quantity") Integer quantity);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.memberDiscountAvailable = true AND p.isActive = true")
    Optional<ResourcePricing> findMemberPricingForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.studentDiscountAvailable = true AND p.isActive = true")
    Optional<ResourcePricing> findStudentPricingForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.depositRequired = true AND p.isActive = true")
    Optional<ResourcePricing> findDepositRequiredPricingForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.peakHourPricing = true AND p.peakStartHour <= :hour AND p.peakEndHour >= :hour AND p.isActive = true")
    List<ResourcePricing> findPeakHourPricingForResource(@Param("resourceId") Long resourceId, @Param("hour") Integer hour);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.weekendPricing = true AND p.isActive = true")
    Optional<ResourcePricing> findWeekendPricingForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.holidayPricing = true AND p.isActive = true")
    Optional<ResourcePricing> findHolidayPricingForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.basePrice >= :minPrice AND p.basePrice <= :maxPrice AND p.isActive = true")
    List<ResourcePricing> findPricingInPriceRangeForResource(@Param("resourceId") Long resourceId, 
                                                           @Param("minPrice") Double minPrice, 
                                                           @Param("maxPrice") Double maxPrice);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.cancellationFeePercentage <= :maxFee AND p.isActive = true")
    List<ResourcePricing> findPricingByMaxCancellationFee(@Param("resourceId") Long resourceId, @Param("maxFee") Double maxFee);
    
    @Query("SELECT p FROM ResourcePricing p WHERE p.resource.id = :resourceId AND " +
           "p.gracePeriodMinutes >= :minGracePeriod AND p.isActive = true")
    List<ResourcePricing> findPricingByMinGracePeriod(@Param("resourceId") Long resourceId, @Param("minGracePeriod") Integer minGracePeriod);
}
