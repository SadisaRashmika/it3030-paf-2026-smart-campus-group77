package com.it3030.smartcampus.member1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_pricing")
public class ResourcePricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private EnhancedResource resource;

    @Column(name = "pricing_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PricingType pricingType = PricingType.HOURLY;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    @Column(name = "currency", nullable = false)
    private String currency = "USD";

    @Column(name = "price_per_unit")
    private Double pricePerUnit;

    @Column(name = "price_per_hour")
    private Double pricePerHour;

    @Column(name = "price_per_day")
    private Double pricePerDay;

    @Column(name = "price_per_week")
    private Double pricePerWeek;

    @Column(name = "price_per_month")
    private Double pricePerMonth;

    @Column(name = "minimum_booking_hours")
    private Integer minimumBookingHours;

    @Column(name = "maximum_booking_hours")
    private Integer maximumBookingHours;

    @Column(name = "deposit_required")
    private Boolean depositRequired = false;

    @Column(name = "deposit_amount")
    private Double depositAmount;

    @Column(name = "cancellation_policy", nullable = false)
    @Enumerated(EnumType.STRING)
    private CancellationPolicy cancellationPolicy = CancellationPolicy.STANDARD;

    @Column(name = "cancellation_fee_percentage")
    private Double cancellationFeePercentage = 10.0;

    @Column(name = "cancellation_fee_hours")
    private Integer cancellationFeeHours = 24;

    @Column(name = "late_fee_percentage")
    private Double lateFeePercentage = 5.0;

    @Column(name = "grace_period_minutes")
    private Integer gracePeriodMinutes = 15;

    @Column(name = "discount_available")
    private Boolean discountAvailable = false;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "discount_minimum_hours")
    private Integer discountMinimumHours;

    @Column(name = "peak_hour_pricing")
    private Boolean peakHourPricing = false;

    @Column(name = "peak_hour_multiplier")
    private Double peakHourMultiplier = 1.5;

    @Column(name = "peak_start_hour")
    private Integer peakStartHour;

    @Column(name = "peak_end_hour")
    private Integer peakEndHour;

    @Column(name = "weekend_pricing")
    private Boolean weekendPricing = false;

    @Column(name = "weekend_multiplier")
    private Double weekendMultiplier = 1.2;

    @Column(name = "holiday_pricing")
    private Boolean holidayPricing = false;

    @Column(name = "holiday_multiplier")
    private Double holidayMultiplier = 1.3;

    @Column(name = "seasonal_pricing")
    private Boolean seasonalPricing = false;

    @Column(name = "seasonal_start_date")
    private LocalDateTime seasonalStartDate;

    @Column(name = "seasonal_end_date")
    private LocalDateTime seasonalEndDate;

    @Column(name = "seasonal_multiplier")
    private Double seasonalMultiplier;

    @Column(name = "bulk_discount_available")
    private Boolean bulkDiscountAvailable = false;

    @Column(name = "bulk_discount_threshold")
    private Integer bulkDiscountThreshold;

    @Column(name = "bulk_discount_percentage")
    private Double bulkDiscountPercentage;

    @Column(name = "member_discount_available")
    private Boolean memberDiscountAvailable = false;

    @Column(name = "member_discount_percentage")
    private Double memberDiscountPercentage;

    @Column(name = "student_discount_available")
    private Boolean studentDiscountAvailable = false;

    @Column(name = "student_discount_percentage")
    private Double studentDiscountPercentage;

    @Column(name = "effective_from", nullable = false)
    private LocalDateTime effectiveFrom = LocalDateTime.now();

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum PricingType {
        HOURLY, DAILY, WEEKLY, MONTHLY, PER_USE, PER_PERSON, PACKAGE
    }

    public enum CancellationPolicy {
        STRICT, MODERATE, STANDARD, FLEXIBLE, FREE_CANCELLATION
    }

    public ResourcePricing() {}

    public ResourcePricing(EnhancedResource resource, PricingType pricingType, Double basePrice) {
        this.resource = resource;
        this.pricingType = pricingType;
        this.basePrice = basePrice;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EnhancedResource getResource() { return resource; }
    public void setResource(EnhancedResource resource) { this.resource = resource; }

    public PricingType getPricingType() { return pricingType; }
    public void setPricingType(PricingType pricingType) { this.pricingType = pricingType; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(Double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    public Double getPricePerHour() { return pricePerHour; }
    public void setPricePerHour(Double pricePerHour) { this.pricePerHour = pricePerHour; }

    public Double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Double pricePerDay) { this.pricePerDay = pricePerDay; }

    public Double getPricePerWeek() { return pricePerWeek; }
    public void setPricePerWeek(Double pricePerWeek) { this.pricePerWeek = pricePerWeek; }

    public Double getPricePerMonth() { return pricePerMonth; }
    public void setPricePerMonth(Double pricePerMonth) { this.pricePerMonth = pricePerMonth; }

    public Integer getMinimumBookingHours() { return minimumBookingHours; }
    public void setMinimumBookingHours(Integer minimumBookingHours) { this.minimumBookingHours = minimumBookingHours; }

    public Integer getMaximumBookingHours() { return maximumBookingHours; }
    public void setMaximumBookingHours(Integer maximumBookingHours) { this.maximumBookingHours = maximumBookingHours; }

    public Boolean getDepositRequired() { return depositRequired; }
    public void setDepositRequired(Boolean depositRequired) { this.depositRequired = depositRequired; }

    public Double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(Double depositAmount) { this.depositAmount = depositAmount; }

    public CancellationPolicy getCancellationPolicy() { return cancellationPolicy; }
    public void setCancellationPolicy(CancellationPolicy cancellationPolicy) { this.cancellationPolicy = cancellationPolicy; }

    public Double getCancellationFeePercentage() { return cancellationFeePercentage; }
    public void setCancellationFeePercentage(Double cancellationFeePercentage) { this.cancellationFeePercentage = cancellationFeePercentage; }

    public Integer getCancellationFeeHours() { return cancellationFeeHours; }
    public void setCancellationFeeHours(Integer cancellationFeeHours) { this.cancellationFeeHours = cancellationFeeHours; }

    public Double getLateFeePercentage() { return lateFeePercentage; }
    public void setLateFeePercentage(Double lateFeePercentage) { this.lateFeePercentage = lateFeePercentage; }

    public Integer getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(Integer gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; }

    public Boolean getDiscountAvailable() { return discountAvailable; }
    public void setDiscountAvailable(Boolean discountAvailable) { this.discountAvailable = discountAvailable; }

    public Double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(Double discountPercentage) { this.discountPercentage = discountPercentage; }

    public Integer getDiscountMinimumHours() { return discountMinimumHours; }
    public void setDiscountMinimumHours(Integer discountMinimumHours) { this.discountMinimumHours = discountMinimumHours; }

    public Boolean getPeakHourPricing() { return peakHourPricing; }
    public void setPeakHourPricing(Boolean peakHourPricing) { this.peakHourPricing = peakHourPricing; }

    public Double getPeakHourMultiplier() { return peakHourMultiplier; }
    public void setPeakHourMultiplier(Double peakHourMultiplier) { this.peakHourMultiplier = peakHourMultiplier; }

    public Integer getPeakStartHour() { return peakStartHour; }
    public void setPeakStartHour(Integer peakStartHour) { this.peakStartHour = peakStartHour; }

    public Integer getPeakEndHour() { return peakEndHour; }
    public void setPeakEndHour(Integer peakEndHour) { this.peakEndHour = peakEndHour; }

    public Boolean getWeekendPricing() { return weekendPricing; }
    public void setWeekendPricing(Boolean weekendPricing) { this.weekendPricing = weekendPricing; }

    public Double getWeekendMultiplier() { return weekendMultiplier; }
    public void setWeekendMultiplier(Double weekendMultiplier) { this.weekendMultiplier = weekendMultiplier; }

    public Boolean getHolidayPricing() { return holidayPricing; }
    public void setHolidayPricing(Boolean holidayPricing) { this.holidayPricing = holidayPricing; }

    public Double getHolidayMultiplier() { return holidayMultiplier; }
    public void setHolidayMultiplier(Double holidayMultiplier) { this.holidayMultiplier = holidayMultiplier; }

    public Boolean getSeasonalPricing() { return seasonalPricing; }
    public void setSeasonalPricing(Boolean seasonalPricing) { this.seasonalPricing = seasonalPricing; }

    public LocalDateTime getSeasonalStartDate() { return seasonalStartDate; }
    public void setSeasonalStartDate(LocalDateTime seasonalStartDate) { this.seasonalStartDate = seasonalStartDate; }

    public LocalDateTime getSeasonalEndDate() { return seasonalEndDate; }
    public void setSeasonalEndDate(LocalDateTime seasonalEndDate) { this.seasonalEndDate = seasonalEndDate; }

    public Double getSeasonalMultiplier() { return seasonalMultiplier; }
    public void setSeasonalMultiplier(Double seasonalMultiplier) { this.seasonalMultiplier = seasonalMultiplier; }

    public Boolean getBulkDiscountAvailable() { return bulkDiscountAvailable; }
    public void setBulkDiscountAvailable(Boolean bulkDiscountAvailable) { this.bulkDiscountAvailable = bulkDiscountAvailable; }

    public Integer getBulkDiscountThreshold() { return bulkDiscountThreshold; }
    public void setBulkDiscountThreshold(Integer bulkDiscountThreshold) { this.bulkDiscountThreshold = bulkDiscountThreshold; }

    public Double getBulkDiscountPercentage() { return bulkDiscountPercentage; }
    public void setBulkDiscountPercentage(Double bulkDiscountPercentage) { this.bulkDiscountPercentage = bulkDiscountPercentage; }

    public Boolean getMemberDiscountAvailable() { return memberDiscountAvailable; }
    public void setMemberDiscountAvailable(Boolean memberDiscountAvailable) { this.memberDiscountAvailable = memberDiscountAvailable; }

    public Double getMemberDiscountPercentage() { return memberDiscountPercentage; }
    public void setMemberDiscountPercentage(Double memberDiscountPercentage) { this.memberDiscountPercentage = memberDiscountPercentage; }

    public Boolean getStudentDiscountAvailable() { return studentDiscountAvailable; }
    public void setStudentDiscountAvailable(Boolean studentDiscountAvailable) { this.studentDiscountAvailable = studentDiscountAvailable; }

    public Double getStudentDiscountPercentage() { return studentDiscountPercentage; }
    public void setStudentDiscountPercentage(Double studentDiscountPercentage) { this.studentDiscountPercentage = studentDiscountPercentage; }

    public LocalDateTime getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDateTime effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDateTime getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDateTime effectiveTo) { this.effectiveTo = effectiveTo; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Business logic methods
    public boolean isCurrentlyEffective() {
        LocalDateTime now = LocalDateTime.now();
        boolean afterStart = now.isAfter(effectiveFrom) || now.isEqual(effectiveFrom);
        boolean beforeEnd = effectiveTo == null || now.isBefore(effectiveTo) || now.isEqual(effectiveTo);
        return afterStart && beforeEnd && isActive;
    }

    public Double calculatePrice(Integer hours, Integer attendees, boolean isPeakHour, boolean isWeekend, boolean isHoliday) {
        Double basePrice = this.basePrice;
        
        // Apply peak hour pricing
        if (peakHourPricing && isPeakHour && peakHourMultiplier != null) {
            basePrice *= peakHourMultiplier;
        }
        
        // Apply weekend pricing
        if (weekendPricing && isWeekend && weekendMultiplier != null) {
            basePrice *= weekendMultiplier;
        }
        
        // Apply holiday pricing
        if (holidayPricing && isHoliday && holidayMultiplier != null) {
            basePrice *= holidayMultiplier;
        }
        
        // Calculate total based on pricing type
        Double totalPrice = 0.0;
        switch (pricingType) {
            case HOURLY:
                totalPrice = basePrice * hours;
                break;
            case DAILY:
                totalPrice = pricePerDay != null ? pricePerDay : basePrice;
                break;
            case WEEKLY:
                totalPrice = pricePerWeek != null ? pricePerWeek : basePrice;
                break;
            case MONTHLY:
                totalPrice = pricePerMonth != null ? pricePerMonth : basePrice;
                break;
            case PER_PERSON:
                totalPrice = basePrice * attendees;
                break;
            case PER_USE:
                totalPrice = basePrice;
                break;
            case PACKAGE:
                totalPrice = basePrice;
                break;
        }
        
        return totalPrice;
    }
}
