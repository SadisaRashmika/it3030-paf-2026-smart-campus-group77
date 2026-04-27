package com.it3030.smartcampus.member1.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_booking_schedules")
public class ResourceBookingSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private EnhancedResource resource;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "booking_title", nullable = false)
    private String bookingTitle;

    @Column(name = "booking_description", columnDefinition = "TEXT")
    private String bookingDescription;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "expected_attendees")
    private Integer expectedAttendees;

    @Column(name = "actual_attendees")
    private Integer actualAttendees;

    @Column(name = "booking_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "priority_level")
    @Enumerated(EnumType.STRING)
    private PriorityLevel priority = PriorityLevel.NORMAL;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "special_requirements", columnDefinition = "TEXT")
    private String specialRequirements;

    @Column(name = "recurring_booking", nullable = false)
    private Boolean recurringBooking = false;

    @Column(name = "recurring_pattern")
    private String recurringPattern;

    @Column(name = "recurring_end_date")
    private LocalDateTime recurringEndDate;

    @Column(name = "approval_notes", columnDefinition = "TEXT")
    private String approvalNotes;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum BookingStatus {
        PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED, NO_SHOW
    }

    public enum PriorityLevel {
        LOW, NORMAL, HIGH, URGENT
    }

    public enum PaymentStatus {
        PENDING, PAID, REFUNDED, PARTIALLY_PAID
    }

    public ResourceBookingSchedule() {}

    public ResourceBookingSchedule(EnhancedResource resource, String userId, String bookingTitle,
                                   LocalDateTime startTime, LocalDateTime endTime) {
        this.resource = resource;
        this.userId = userId;
        this.bookingTitle = bookingTitle;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EnhancedResource getResource() { return resource; }
    public void setResource(EnhancedResource resource) { this.resource = resource; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookingTitle() { return bookingTitle; }
    public void setBookingTitle(String bookingTitle) { this.bookingTitle = bookingTitle; }

    public String getBookingDescription() { return bookingDescription; }
    public void setBookingDescription(String bookingDescription) { this.bookingDescription = bookingDescription; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getExpectedAttendees() { return expectedAttendees; }
    public void setExpectedAttendees(Integer expectedAttendees) { this.expectedAttendees = expectedAttendees; }

    public Integer getActualAttendees() { return actualAttendees; }
    public void setActualAttendees(Integer actualAttendees) { this.actualAttendees = actualAttendees; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public PriorityLevel getPriorityLevel() { return priority; }
    public void setPriorityLevel(PriorityLevel priorityLevel) { this.priority = priorityLevel; }

    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getSpecialRequirements() { return specialRequirements; }
    public void setSpecialRequirements(String specialRequirements) { this.specialRequirements = specialRequirements; }

    public Boolean getRecurringBooking() { return recurringBooking; }
    public void setRecurringBooking(Boolean recurringBooking) { this.recurringBooking = recurringBooking; }

    public String getRecurringPattern() { return recurringPattern; }
    public void setRecurringPattern(String recurringPattern) { this.recurringPattern = recurringPattern; }

    public LocalDateTime getRecurringEndDate() { return recurringEndDate; }
    public void setRecurringEndDate(LocalDateTime recurringEndDate) { this.recurringEndDate = recurringEndDate; }

    public String getApprovalNotes() { return approvalNotes; }
    public void setApprovalNotes(String approvalNotes) { this.approvalNotes = approvalNotes; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
