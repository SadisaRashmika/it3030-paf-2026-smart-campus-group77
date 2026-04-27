package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourcePricing;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.repository.ResourcePricingRepository;
import com.it3030.smartcampus.member1.repository.EnhancedResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourcePricingService {

    @Autowired
    private ResourcePricingRepository pricingRepository;

    @Autowired
    private EnhancedResourceRepository resourceRepository;

    public List<ResourcePricing> getAllPricing() {
        return pricingRepository.findAll();
    }

    public Optional<ResourcePricing> getPricingById(Long id) {
        return pricingRepository.findById(id);
    }

    public List<ResourcePricing> getPricingByResource(Long resourceId) {
        return pricingRepository.findPricingByResourceId(resourceId);
    }

    public List<ResourcePricing> getActivePricingByResource(Long resourceId) {
        return pricingRepository.findActivePricingForResource(resourceId);
    }

    public Optional<ResourcePricing> getCurrentPricingForResource(Long resourceId) {
        return pricingRepository.findCurrentPricingForResource(resourceId);
    }

    public Optional<ResourcePricing> getEffectivePricingForResource(Long resourceId, LocalDateTime date) {
        return pricingRepository.findEffectivePricingForResource(resourceId, date);
    }

    public ResourcePricing createPricing(ResourcePricing pricing) {
        // Validate resource exists
        EnhancedResource resource = resourceRepository.findById(pricing.getResource().getId())
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + pricing.getResource().getId()));

        // Validate pricing data
        if (pricing.getBasePrice() == null || pricing.getBasePrice() <= 0) {
            throw new IllegalArgumentException("Base price must be positive");
        }

        if (pricing.getEffectiveFrom() == null) {
            pricing.setEffectiveFrom(LocalDateTime.now());
        }

        if (pricing.getEffectiveTo() != null && pricing.getEffectiveTo().isBefore(pricing.getEffectiveFrom())) {
            throw new IllegalArgumentException("Effective to date must be after effective from date");
        }

        // Validate discount percentages
        if (pricing.getDiscountPercentage() != null && (pricing.getDiscountPercentage() < 0 || pricing.getDiscountPercentage() > 100)) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }

        // Validate multipliers
        validateMultipliers(pricing);

        pricing.setResource(resource);
        pricing.setCreatedAt(LocalDateTime.now());
        pricing.setUpdatedAt(LocalDateTime.now());

        return pricingRepository.save(pricing);
    }

    public ResourcePricing updatePricing(Long id, ResourcePricing pricingDetails) {
        ResourcePricing pricing = pricingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing not found with id: " + id));

        // Validate pricing data
        if (pricingDetails.getBasePrice() != null && pricingDetails.getBasePrice() <= 0) {
            throw new IllegalArgumentException("Base price must be positive");
        }

        if (pricingDetails.getEffectiveTo() != null && pricing.getEffectiveFrom() != null && 
            pricingDetails.getEffectiveTo().isBefore(pricingDetails.getEffectiveFrom())) {
            throw new IllegalArgumentException("Effective to date must be after effective from date");
        }

        // Validate discount percentages
        if (pricingDetails.getDiscountPercentage() != null && 
            (pricingDetails.getDiscountPercentage() < 0 || pricingDetails.getDiscountPercentage() > 100)) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }

        // Validate multipliers
        validateMultipliers(pricingDetails);

        pricing.setBasePrice(pricingDetails.getBasePrice());
        pricing.setCurrency(pricingDetails.getCurrency());
        pricing.setPricingType(pricingDetails.getPricingType());
        pricing.setPricePerUnit(pricingDetails.getPricePerUnit());
        pricing.setPricePerHour(pricingDetails.getPricePerHour());
        pricing.setPricePerDay(pricingDetails.getPricePerDay());
        pricing.setPricePerWeek(pricingDetails.getPricePerWeek());
        pricing.setPricePerMonth(pricingDetails.getPricePerMonth());
        pricing.setMinimumBookingHours(pricingDetails.getMinimumBookingHours());
        pricing.setMaximumBookingHours(pricingDetails.getMaximumBookingHours());
        pricing.setDepositRequired(pricingDetails.getDepositRequired());
        pricing.setDepositAmount(pricingDetails.getDepositAmount());
        pricing.setCancellationPolicy(pricingDetails.getCancellationPolicy());
        pricing.setCancellationFeePercentage(pricingDetails.getCancellationFeePercentage());
        pricing.setCancellationFeeHours(pricingDetails.getCancellationFeeHours());
        pricing.setLateFeePercentage(pricingDetails.getLateFeePercentage());
        pricing.setGracePeriodMinutes(pricingDetails.getGracePeriodMinutes());
        pricing.setDiscountAvailable(pricingDetails.getDiscountAvailable());
        pricing.setDiscountPercentage(pricingDetails.getDiscountPercentage());
        pricing.setDiscountMinimumHours(pricingDetails.getDiscountMinimumHours());
        pricing.setPeakHourPricing(pricingDetails.getPeakHourPricing());
        pricing.setPeakHourMultiplier(pricingDetails.getPeakHourMultiplier());
        pricing.setPeakStartHour(pricingDetails.getPeakStartHour());
        pricing.setPeakEndHour(pricingDetails.getPeakEndHour());
        pricing.setWeekendPricing(pricingDetails.getWeekendPricing());
        pricing.setWeekendMultiplier(pricingDetails.getWeekendMultiplier());
        pricing.setHolidayPricing(pricingDetails.getHolidayPricing());
        pricing.setHolidayMultiplier(pricingDetails.getHolidayMultiplier());
        pricing.setSeasonalPricing(pricingDetails.getSeasonalPricing());
        pricing.setSeasonalStartDate(pricingDetails.getSeasonalStartDate());
        pricing.setSeasonalEndDate(pricingDetails.getSeasonalEndDate());
        pricing.setSeasonalMultiplier(pricingDetails.getSeasonalMultiplier());
        pricing.setBulkDiscountAvailable(pricingDetails.getBulkDiscountAvailable());
        pricing.setBulkDiscountThreshold(pricingDetails.getBulkDiscountThreshold());
        pricing.setBulkDiscountPercentage(pricingDetails.getBulkDiscountPercentage());
        pricing.setMemberDiscountAvailable(pricingDetails.getMemberDiscountAvailable());
        pricing.setMemberDiscountPercentage(pricingDetails.getMemberDiscountPercentage());
        pricing.setStudentDiscountAvailable(pricingDetails.getStudentDiscountAvailable());
        pricing.setStudentDiscountPercentage(pricingDetails.getStudentDiscountPercentage());
        pricing.setEffectiveFrom(pricingDetails.getEffectiveFrom());
        pricing.setEffectiveTo(pricingDetails.getEffectiveTo());
        pricing.setIsActive(pricingDetails.getIsActive());
        pricing.setUpdatedAt(LocalDateTime.now());

        return pricingRepository.save(pricing);
    }

    public void deletePricing(Long id) {
        ResourcePricing pricing = pricingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing not found with id: " + id));

        pricingRepository.delete(pricing);
    }

    public List<ResourcePricing> getPricingByType(ResourcePricing.PricingType pricingType) {
        return pricingRepository.findByPricingType(pricingType);
    }

    public List<ResourcePricing> getPricingByCurrency(String currency) {
        return pricingRepository.findByCurrency(currency);
    }

    public List<ResourcePricing> getPricingWithDeposit() {
        return pricingRepository.findPricingWithDeposit();
    }

    public List<ResourcePricing> getPricingWithDiscount() {
        return pricingRepository.findPricingWithDiscount();
    }

    public List<ResourcePricing> getPricingWithPeakHourPricing() {
        return pricingRepository.findPricingWithPeakHourPricing();
    }

    public List<ResourcePricing> getPricingWithWeekendPricing() {
        return pricingRepository.findPricingWithWeekendPricing();
    }

    public List<ResourcePricing> getPricingWithHolidayPricing() {
        return pricingRepository.findPricingWithHolidayPricing();
    }

    public List<ResourcePricing> getPricingWithSeasonalPricing() {
        return pricingRepository.findPricingWithSeasonalPricing();
    }

    public List<ResourcePricing> getPricingWithBulkDiscount() {
        return pricingRepository.findPricingWithBulkDiscount();
    }

    public List<ResourcePricing> getPricingWithMemberDiscount() {
        return pricingRepository.findPricingWithMemberDiscount();
    }

    public List<ResourcePricing> getPricingWithStudentDiscount() {
        return pricingRepository.findPricingWithStudentDiscount();
    }

    public List<ResourcePricing> getPricingByPriceRange(Double minPrice, Double maxPrice) {
        return pricingRepository.findByPriceRange(minPrice, maxPrice);
    }

    public List<ResourcePricing> getPricingByCancellationPolicy(ResourcePricing.CancellationPolicy policy) {
        return pricingRepository.findByCancellationPolicy(policy);
    }

    public List<ResourcePricing> getFuturePricing(LocalDateTime date) {
        return pricingRepository.findFuturePricing(date);
    }

    public List<ResourcePricing> getExpiredPricing(LocalDateTime date) {
        return pricingRepository.findExpiredPricing(date);
    }

    public List<ResourcePricing> getCurrentlyEffectivePricing(LocalDateTime date) {
        return pricingRepository.findCurrentlyEffectivePricing(date);
    }

    public Double getMinimumActivePrice() {
        return pricingRepository.findMinimumActivePrice();
    }

    public Double getMaximumActivePrice() {
        return pricingRepository.findMaximumActivePrice();
    }

    public Double getAverageActivePrice() {
        return pricingRepository.findAverageActivePrice();
    }

    public Optional<ResourcePricing> getPricingByTypeForResource(Long resourceId, ResourcePricing.PricingType pricingType) {
        return pricingRepository.findPricingByTypeForResource(resourceId, pricingType);
    }

    public List<ResourcePricing> getAffordablePricingForResource(Long resourceId, Double maxPrice) {
        return pricingRepository.findAffordablePricingForResource(resourceId, maxPrice);
    }

    public List<ResourcePricing> getPricingForBookingDuration(Long resourceId, Integer hours) {
        return pricingRepository.findPricingForBookingDuration(resourceId, hours);
    }

    public Optional<ResourcePricing> getPricingByCancellationPolicyForResource(Long resourceId, ResourcePricing.CancellationPolicy policy) {
        return pricingRepository.findPricingByCancellationPolicyForResource(resourceId, policy);
    }

    public long countActivePricing() {
        return pricingRepository.countActivePricing();
    }

    public long countActivePricingForResource(Long resourceId) {
        return pricingRepository.countActivePricingForResource(resourceId);
    }

    public long countPricingWithDiscount() {
        return pricingRepository.countPricingWithDiscount();
    }

    public Double calculatePrice(Long resourceId, Integer hours, Integer attendees, boolean isPeakHour, boolean isWeekend, boolean isHoliday) {
        Optional<ResourcePricing> pricingOpt = getCurrentPricingForResource(resourceId);
        
        if (!pricingOpt.isPresent()) {
            throw new RuntimeException("No active pricing found for resource");
        }

        ResourcePricing pricing = pricingOpt.get();
        return pricing.calculatePrice(hours, attendees, isPeakHour, isWeekend, isHoliday);
    }

    public Double calculatePriceWithDiscounts(Long resourceId, Integer hours, Integer attendees, boolean isPeakHour, boolean isWeekend, boolean isHoliday, boolean isMember, boolean isStudent) {
        Optional<ResourcePricing> pricingOpt = getCurrentPricingForResource(resourceId);
        
        if (!pricingOpt.isPresent()) {
            throw new RuntimeException("No active pricing found for resource");
        }

        ResourcePricing pricing = pricingOpt.get();
        Double basePrice = pricing.calculatePrice(hours, attendees, isPeakHour, isWeekend, isHoliday);

        // Apply member discount
        if (isMember && pricing.getMemberDiscountAvailable() && pricing.getMemberDiscountPercentage() != null) {
            basePrice = basePrice * (1 - pricing.getMemberDiscountPercentage() / 100);
        }

        // Apply student discount
        if (isStudent && pricing.getStudentDiscountAvailable() && pricing.getStudentDiscountPercentage() != null) {
            basePrice = basePrice * (1 - pricing.getStudentDiscountPercentage() / 100);
        }

        // Apply bulk discount
        if (pricing.getBulkDiscountAvailable() && pricing.getBulkDiscountThreshold() != null && 
            pricing.getBulkDiscountPercentage() != null && hours >= pricing.getBulkDiscountThreshold()) {
            basePrice = basePrice * (1 - pricing.getBulkDiscountPercentage() / 100);
        }

        // Apply regular discount
        if (pricing.getDiscountAvailable() && pricing.getDiscountPercentage() != null && 
            pricing.getDiscountMinimumHours() != null && hours >= pricing.getDiscountMinimumHours()) {
            basePrice = basePrice * (1 - pricing.getDiscountPercentage() / 100);
        }

        return basePrice;
    }

    public Double calculateCancellationFee(Long resourceId, Double originalPrice, LocalDateTime bookingTime, LocalDateTime cancellationTime) {
        Optional<ResourcePricing> pricingOpt = getCurrentPricingForResource(resourceId);
        
        if (!pricingOpt.isPresent()) {
            return 0.0;
        }

        ResourcePricing pricing = pricingOpt.get();
        
        // Check if cancellation is within fee period
        long hoursUntilBooking = java.time.Duration.between(cancellationTime, bookingTime).toHours();
        
        if (hoursUntilBooking >= pricing.getCancellationFeeHours()) {
            return 0.0;
        }

        return originalPrice * (pricing.getCancellationFeePercentage() / 100);
    }

    public Double calculateLateFee(Long resourceId, Double originalPrice, LocalDateTime endTime, LocalDateTime actualEndTime) {
        Optional<ResourcePricing> pricingOpt = getCurrentPricingForResource(resourceId);
        
        if (!pricingOpt.isPresent()) {
            return 0.0;
        }

        ResourcePricing pricing = pricingOpt.get();
        
        // Check if late
        long minutesLate = java.time.Duration.between(endTime, actualEndTime).toMinutes();
        
        if (minutesLate <= pricing.getGracePeriodMinutes()) {
            return 0.0;
        }

        return originalPrice * (pricing.getLateFeePercentage() / 100);
    }

    public boolean isPeakHour(Long resourceId, LocalDateTime dateTime) {
        Optional<ResourcePricing> pricingOpt = getCurrentPricingForResource(resourceId);
        
        if (!pricingOpt.isPresent() || !pricingOpt.get().getPeakHourPricing()) {
            return false;
        }

        ResourcePricing pricing = pricingOpt.get();
        int hour = dateTime.getHour();
        
        return pricing.getPeakStartHour() != null && pricing.getPeakEndHour() != null &&
               hour >= pricing.getPeakStartHour() && hour <= pricing.getPeakEndHour();
    }

    public boolean isWeekend(LocalDateTime dateTime) {
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // Saturday or Sunday
    }

    public boolean isHoliday(LocalDateTime dateTime) {
        // This would typically check against a holiday calendar
        // For now, we'll return false
        return false;
    }

    public boolean isSeasonalPricingActive(Long resourceId, LocalDateTime dateTime) {
        Optional<ResourcePricing> pricingOpt = getCurrentPricingForResource(resourceId);
        
        if (!pricingOpt.isPresent() || !pricingOpt.get().getSeasonalPricing()) {
            return false;
        }

        ResourcePricing pricing = pricingOpt.get();
        
        return pricing.getSeasonalStartDate() != null && pricing.getSeasonalEndDate() != null &&
               !dateTime.isBefore(pricing.getSeasonalStartDate()) && !dateTime.isAfter(pricing.getSeasonalEndDate());
    }

    public ResourcePricing activatePricing(Long id) {
        ResourcePricing pricing = pricingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing not found with id: " + id));

        pricing.setIsActive(true);
        pricing.setUpdatedAt(LocalDateTime.now());

        return pricingRepository.save(pricing);
    }

    public ResourcePricing deactivatePricing(Long id) {
        ResourcePricing pricing = pricingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing not found with id: " + id));

        pricing.setIsActive(false);
        pricing.setUpdatedAt(LocalDateTime.now());

        return pricingRepository.save(pricing);
    }

    public ResourcePricing scheduleFuturePricing(Long resourceId, ResourcePricing futurePricing) {
        // Validate resource exists
        EnhancedResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + resourceId));

        if (futurePricing.getEffectiveFrom() == null || !futurePricing.getEffectiveFrom().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Future pricing must have effective from date in the future");
        }

        futurePricing.setResource(resource);
        futurePricing.setCreatedAt(LocalDateTime.now());
        futurePricing.setUpdatedAt(LocalDateTime.now());

        return pricingRepository.save(futurePricing);
    }

    public void processExpiredPricing() {
        List<ResourcePricing> expiredPricing = getExpiredPricing(LocalDateTime.now());
        
        for (ResourcePricing pricing : expiredPricing) {
            pricing.setIsActive(false);
            pricing.setUpdatedAt(LocalDateTime.now());
            pricingRepository.save(pricing);
        }
    }

    public void activateFuturePricing() {
        List<ResourcePricing> futurePricing = getFuturePricing(LocalDateTime.now());
        
        for (ResourcePricing pricing : futurePricing) {
            if (pricing.getEffectiveFrom().isBefore(LocalDateTime.now()) || pricing.getEffectiveFrom().isEqual(LocalDateTime.now())) {
                pricing.setIsActive(true);
                pricing.setUpdatedAt(LocalDateTime.now());
                pricingRepository.save(pricing);
            }
        }
    }

    private void validateMultipliers(ResourcePricing pricing) {
        if (pricing.getPeakHourMultiplier() != null && pricing.getPeakHourMultiplier() < 0) {
            throw new IllegalArgumentException("Peak hour multiplier must be non-negative");
        }

        if (pricing.getWeekendMultiplier() != null && pricing.getWeekendMultiplier() < 0) {
            throw new IllegalArgumentException("Weekend multiplier must be non-negative");
        }

        if (pricing.getHolidayMultiplier() != null && pricing.getHolidayMultiplier() < 0) {
            throw new IllegalArgumentException("Holiday multiplier must be non-negative");
        }

        if (pricing.getSeasonalMultiplier() != null && pricing.getSeasonalMultiplier() < 0) {
            throw new IllegalArgumentException("Seasonal multiplier must be non-negative");
        }
    }

    public void seedDefaultPricing() {
        if (pricingRepository.count() == 0) {
            // This would be populated with default pricing for existing resources
            // Implementation would depend on the existing resources
        }
    }
}
