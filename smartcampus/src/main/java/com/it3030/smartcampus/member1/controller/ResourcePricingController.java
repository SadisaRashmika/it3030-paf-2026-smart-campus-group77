package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourcePricing;
import com.it3030.smartcampus.member1.service.ResourcePricingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/pricing")
public class ResourcePricingController {

    @Autowired
    private ResourcePricingService pricingService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getAllPricing() {
        return pricingService.getAllPricing();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getPricingById(@PathVariable Long id) {
        Optional<ResourcePricing> pricing = pricingService.getPricingById(id);
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingByResource(@PathVariable Long resourceId) {
        return pricingService.getPricingByResource(resourceId);
    }

    @GetMapping("/resource/{resourceId}/active")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getActivePricingByResource(@PathVariable Long resourceId) {
        return pricingService.getActivePricingByResource(resourceId);
    }

    @GetMapping("/resource/{resourceId}/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getCurrentPricingForResource(@PathVariable Long resourceId) {
        Optional<ResourcePricing> pricing = pricingService.getCurrentPricingForResource(resourceId);
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/effective")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getEffectivePricingForResource(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        Optional<ResourcePricing> pricing = pricingService.getEffectivePricingForResource(resourceId, date);
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> createPricing(@RequestBody ResourcePricing pricing) {
        try {
            ResourcePricing createdPricing = pricingService.createPricing(pricing);
            return ResponseEntity.ok(createdPricing);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> updatePricing(@PathVariable Long id, @RequestBody ResourcePricing pricingDetails) {
        try {
            ResourcePricing updatedPricing = pricingService.updatePricing(id, pricingDetails);
            return ResponseEntity.ok(updatedPricing);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Void> deletePricing(@PathVariable Long id) {
        try {
            pricingService.deletePricing(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/type/{pricingType}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingByType(@PathVariable ResourcePricing.PricingType pricingType) {
        return pricingService.getPricingByType(pricingType);
    }

    @GetMapping("/currency/{currency}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingByCurrency(@PathVariable String currency) {
        return pricingService.getPricingByCurrency(currency);
    }

    @GetMapping("/with-deposit")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithDeposit() {
        return pricingService.getPricingWithDeposit();
    }

    @GetMapping("/with-discount")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithDiscount() {
        return pricingService.getPricingWithDiscount();
    }

    @GetMapping("/with-peak-hour")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithPeakHourPricing() {
        return pricingService.getPricingWithPeakHourPricing();
    }

    @GetMapping("/with-weekend")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithWeekendPricing() {
        return pricingService.getPricingWithWeekendPricing();
    }

    @GetMapping("/with-holiday")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithHolidayPricing() {
        return pricingService.getPricingWithHolidayPricing();
    }

    @GetMapping("/with-seasonal")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithSeasonalPricing() {
        return pricingService.getPricingWithSeasonalPricing();
    }

    @GetMapping("/with-bulk-discount")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithBulkDiscount() {
        return pricingService.getPricingWithBulkDiscount();
    }

    @GetMapping("/with-member-discount")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithMemberDiscount() {
        return pricingService.getPricingWithMemberDiscount();
    }

    @GetMapping("/with-student-discount")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingWithStudentDiscount() {
        return pricingService.getPricingWithStudentDiscount();
    }

    @GetMapping("/price-range")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingByPriceRange(
            @RequestParam Double minPrice, @RequestParam Double maxPrice) {
        return pricingService.getPricingByPriceRange(minPrice, maxPrice);
    }

    @GetMapping("/cancellation-policy/{policy}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingByCancellationPolicy(@PathVariable ResourcePricing.CancellationPolicy policy) {
        return pricingService.getPricingByCancellationPolicy(policy);
    }

    @GetMapping("/future")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourcePricing> getFuturePricing(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return pricingService.getFuturePricing(date);
    }

    @GetMapping("/expired")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourcePricing> getExpiredPricing(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return pricingService.getExpiredPricing(date);
    }

    @GetMapping("/currently-effective")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getCurrentlyEffectivePricing(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return pricingService.getCurrentlyEffectivePricing(date);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Object> getPricingStatistics() {
        Double minPrice = pricingService.getMinimumActivePrice();
        Double maxPrice = pricingService.getMaximumActivePrice();
        Double avgPrice = pricingService.getAverageActivePrice();
        long activeCount = pricingService.countActivePricing();
        long discountCount = pricingService.countPricingWithDiscount();
        
        return ResponseEntity.ok(java.util.Map.of(
            "minimumPrice", minPrice != null ? minPrice : 0.0,
            "maximumPrice", maxPrice != null ? maxPrice : 0.0,
            "averagePrice", avgPrice != null ? avgPrice : 0.0,
            "activeCount", activeCount,
            "discountCount", discountCount
        ));
    }

    @GetMapping("/resource/{resourceId}/type/{pricingType}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getPricingByTypeForResource(@PathVariable Long resourceId, @PathVariable ResourcePricing.PricingType pricingType) {
        Optional<ResourcePricing> pricing = pricingService.getPricingByTypeForResource(resourceId, pricingType);
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/affordable")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getAffordablePricingForResource(@PathVariable Long resourceId, @RequestParam Double maxPrice) {
        return pricingService.getAffordablePricingForResource(resourceId, maxPrice);
    }

    @GetMapping("/resource/{resourceId}/duration/{hours}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingForBookingDuration(@PathVariable Long resourceId, @PathVariable Integer hours) {
        return pricingService.getPricingForBookingDuration(resourceId, hours);
    }

    @GetMapping("/resource/{resourceId}/cancellation/{policy}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getPricingByCancellationPolicyForResource(@PathVariable Long resourceId, @PathVariable ResourcePricing.CancellationPolicy policy) {
        Optional<ResourcePricing> pricing = pricingService.getPricingByCancellationPolicyForResource(resourceId, policy);
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countActivePricingForResource(@PathVariable Long resourceId) {
        long count = pricingService.countActivePricingForResource(resourceId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/calculate-price/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> calculatePrice(
            @PathVariable Long resourceId,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            Integer hours = Integer.valueOf(request.get("hours").toString());
            Integer attendees = request.get("attendees") != null ? Integer.valueOf(request.get("attendees").toString()) : 1;
            Boolean isPeakHour = request.get("isPeakHour") != null ? Boolean.valueOf(request.get("isPeakHour").toString()) : false;
            Boolean isWeekend = request.get("isWeekend") != null ? Boolean.valueOf(request.get("isWeekend").toString()) : false;
            Boolean isHoliday = request.get("isHoliday") != null ? Boolean.valueOf(request.get("isHoliday").toString()) : false;
            
            Double price = pricingService.calculatePrice(resourceId, hours, attendees, isPeakHour, isWeekend, isHoliday);
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(0.0);
        }
    }

    @PostMapping("/calculate-price-with-discounts/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> calculatePriceWithDiscounts(
            @PathVariable Long resourceId,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            Integer hours = Integer.valueOf(request.get("hours").toString());
            Integer attendees = request.get("attendees") != null ? Integer.valueOf(request.get("attendees").toString()) : 1;
            Boolean isPeakHour = request.get("isPeakHour") != null ? Boolean.valueOf(request.get("isPeakHour").toString()) : false;
            Boolean isWeekend = request.get("isWeekend") != null ? Boolean.valueOf(request.get("isWeekend").toString()) : false;
            Boolean isHoliday = request.get("isHoliday") != null ? Boolean.valueOf(request.get("isHoliday").toString()) : false;
            Boolean isMember = request.get("isMember") != null ? Boolean.valueOf(request.get("isMember").toString()) : false;
            Boolean isStudent = request.get("isStudent") != null ? Boolean.valueOf(request.get("isStudent").toString()) : false;
            
            Double price = pricingService.calculatePriceWithDiscounts(resourceId, hours, attendees, isPeakHour, isWeekend, isHoliday, isMember, isStudent);
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(0.0);
        }
    }

    @PostMapping("/calculate-cancellation-fee/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> calculateCancellationFee(
            @PathVariable Long resourceId,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            Double originalPrice = Double.valueOf(request.get("originalPrice").toString());
            LocalDateTime bookingTime = LocalDateTime.parse(request.get("bookingTime").toString());
            LocalDateTime cancellationTime = LocalDateTime.parse(request.get("cancellationTime").toString());
            
            Double fee = pricingService.calculateCancellationFee(resourceId, originalPrice, bookingTime, cancellationTime);
            return ResponseEntity.ok(fee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(0.0);
        }
    }

    @PostMapping("/calculate-late-fee/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Double> calculateLateFee(
            @PathVariable Long resourceId,
            @RequestBody java.util.Map<String, Object> request) {
        try {
            Double originalPrice = Double.valueOf(request.get("originalPrice").toString());
            LocalDateTime endTime = LocalDateTime.parse(request.get("endTime").toString());
            LocalDateTime actualEndTime = LocalDateTime.parse(request.get("actualEndTime").toString());
            
            Double fee = pricingService.calculateLateFee(resourceId, originalPrice, endTime, actualEndTime);
            return ResponseEntity.ok(fee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(0.0);
        }
    }

    @GetMapping("/is-peak-hour/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isPeakHour(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        boolean isPeakHour = pricingService.isPeakHour(resourceId, dateTime);
        return ResponseEntity.ok(isPeakHour);
    }

    @GetMapping("/is-weekend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isWeekend(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        boolean isWeekend = pricingService.isWeekend(dateTime);
        return ResponseEntity.ok(isWeekend);
    }

    @GetMapping("/is-holiday")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isHoliday(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        boolean isHoliday = pricingService.isHoliday(dateTime);
        return ResponseEntity.ok(isHoliday);
    }

    @GetMapping("/is-seasonal/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isSeasonalPricingActive(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        boolean isSeasonal = pricingService.isSeasonalPricingActive(resourceId, dateTime);
        return ResponseEntity.ok(isSeasonal);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourcePricing> activatePricing(@PathVariable Long id) {
        try {
            ResourcePricing pricing = pricingService.activatePricing(id);
            return ResponseEntity.ok(pricing);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<ResourcePricing> deactivatePricing(@PathVariable Long id) {
        try {
            ResourcePricing pricing = pricingService.deactivatePricing(id);
            return ResponseEntity.ok(pricing);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/schedule-future/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> scheduleFuturePricing(@PathVariable Long resourceId, @RequestBody ResourcePricing futurePricing) {
        try {
            ResourcePricing pricing = pricingService.scheduleFuturePricing(resourceId, futurePricing);
            return ResponseEntity.ok(pricing);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/process-expired")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> processExpiredPricing() {
        pricingService.processExpiredPricing();
        return ResponseEntity.ok("Expired pricing processed successfully");
    }

    @PostMapping("/activate-future")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> activateFuturePricing() {
        pricingService.activateFuturePricing();
        return ResponseEntity.ok("Future pricing activated successfully");
    }

    @GetMapping("/resource/{resourceId}/price-range")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingInPriceRangeForResource(
            @PathVariable Long resourceId,
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return pricingService.getPricingByPriceRange(minPrice, maxPrice).stream()
                .filter(p -> p.getResource().getId().equals(resourceId))
                .toList();
    }

    @GetMapping("/resource/{resourceId}/max-cancellation-fee")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingByMaxCancellationFee(@PathVariable Long resourceId, @RequestParam Double maxFee) {
        return pricingService.getPricingByCancellationPolicy(ResourcePricing.CancellationPolicy.STANDARD).stream()
                .filter(p -> p.getResource().getId().equals(resourceId) && 
                           (p.getCancellationFeePercentage() == null || p.getCancellationFeePercentage() <= maxFee))
                .toList();
    }

    @GetMapping("/resource/{resourceId}/min-grace-period")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPricingByMinGracePeriod(@PathVariable Long resourceId, @RequestParam Integer minGracePeriod) {
        return pricingService.getAllPricing().stream()
                .filter(p -> p.getResource().getId().equals(resourceId) && 
                           (p.getGracePeriodMinutes() == null || p.getGracePeriodMinutes() >= minGracePeriod))
                .toList();
    }

    @GetMapping("/resource/{resourceId}/member")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getMemberPricingForResource(@PathVariable Long resourceId) {
        Optional<ResourcePricing> pricing = pricingService.getPricingWithMemberDiscount().stream()
                .filter(p -> p.getResource().getId().equals(resourceId))
                .findFirst();
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/student")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getStudentPricingForResource(@PathVariable Long resourceId) {
        Optional<ResourcePricing> pricing = pricingService.getPricingWithStudentDiscount().stream()
                .filter(p -> p.getResource().getId().equals(resourceId))
                .findFirst();
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getDepositRequiredPricingForResource(@PathVariable Long resourceId) {
        Optional<ResourcePricing> pricing = pricingService.getPricingWithDeposit().stream()
                .filter(p -> p.getResource().getId().equals(resourceId))
                .findFirst();
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/weekend")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getWeekendPricingForResource(@PathVariable Long resourceId) {
        Optional<ResourcePricing> pricing = pricingService.getPricingWithWeekendPricing().stream()
                .filter(p -> p.getResource().getId().equals(resourceId))
                .findFirst();
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/holiday")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourcePricing> getHolidayPricingForResource(@PathVariable Long resourceId) {
        Optional<ResourcePricing> pricing = pricingService.getPricingWithHolidayPricing().stream()
                .filter(p -> p.getResource().getId().equals(resourceId))
                .findFirst();
        return pricing.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}/peak-hour/{hour}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getPeakHourPricingForResource(@PathVariable Long resourceId, @PathVariable Integer hour) {
        return pricingService.getPricingWithPeakHourPricing().stream()
                .filter(p -> p.getResource().getId().equals(resourceId) &&
                           (p.getPeakStartHour() == null || p.getPeakEndHour() == null ||
                            (hour >= p.getPeakStartHour() && hour <= p.getPeakEndHour())))
                .toList();
    }

    @GetMapping("/resource/{resourceId}/bulk-discount/{quantity}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourcePricing> getBulkDiscountPricingForResource(@PathVariable Long resourceId, @PathVariable Integer quantity) {
        return pricingService.getPricingWithBulkDiscount().stream()
                .filter(p -> p.getResource().getId().equals(resourceId) &&
                           (p.getBulkDiscountThreshold() == null || quantity >= p.getBulkDiscountThreshold()))
                .toList();
    }

    @PostMapping("/seed")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> seedDefaultPricing() {
        pricingService.seedDefaultPricing();
        return ResponseEntity.ok("Default pricing seeded successfully");
    }
}
