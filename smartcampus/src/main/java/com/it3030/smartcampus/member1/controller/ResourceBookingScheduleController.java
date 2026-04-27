package com.it3030.smartcampus.member1.controller;

import com.it3030.smartcampus.member1.model.ResourceBookingSchedule;
import com.it3030.smartcampus.member1.service.ResourceBookingScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/member1/bookings")
public class ResourceBookingScheduleController {

    @Autowired
    private ResourceBookingScheduleService bookingService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceBookingSchedule> getBookingById(@PathVariable Long id) {
        Optional<ResourceBookingSchedule> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getBookingsByResource(@PathVariable Long resourceId) {
        return bookingService.getBookingsByResource(resourceId);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getBookingsByUser(@PathVariable String userId) {
        return bookingService.getBookingsByUser(userId);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createBooking(@RequestBody ResourceBookingSchedule booking) {
        try {
            ResourceBookingSchedule createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.ok(createdBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody ResourceBookingSchedule bookingDetails) {
        try {
            ResourceBookingSchedule updatedBooking = bookingService.updateBooking(id, bookingDetails);
            return ResponseEntity.ok(updatedBooking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Internal server error"));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> approveBooking(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        try {
            String approvedBy = request.get("approvedBy");
            String approvalNotes = request.get("approvalNotes");
            
            ResourceBookingSchedule booking = bookingService.approveBooking(id, approvedBy, approvalNotes);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> rejectBooking(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        try {
            String approvedBy = request.get("approvedBy");
            String rejectionReason = request.get("rejectionReason");
            
            ResourceBookingSchedule booking = bookingService.rejectBooking(id, approvedBy, rejectionReason);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        try {
            String cancellationReason = request.get("cancellationReason");
            
            ResourceBookingSchedule booking = bookingService.cancelBooking(id, cancellationReason);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> completeBooking(@PathVariable Long id, @RequestBody java.util.Map<String, Object> request) {
        try {
            Integer actualAttendees = request.get("actualAttendees") != null ? 
                Integer.valueOf(request.get("actualAttendees").toString()) : null;
            
            ResourceBookingSchedule booking = bookingService.completeBooking(id, actualAttendees);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/no-show")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> markNoShow(@PathVariable Long id) {
        try {
            ResourceBookingSchedule booking = bookingService.markNoShow(id);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getBookingsByStatus(@PathVariable ResourceBookingSchedule.BookingStatus status) {
        return bookingService.getBookingsByStatus(status);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getPendingBookings() {
        return bookingService.getPendingBookings();
    }

    @GetMapping("/upcoming/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getUpcomingBookingsForResource(@PathVariable Long resourceId) {
        return bookingService.getUpcomingBookingsForResource(resourceId);
    }

    @GetMapping("/upcoming/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getUpcomingBookingsForUser(@PathVariable String userId) {
        return bookingService.getUpcomingBookingsForUser(userId);
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getActiveBookings() {
        return bookingService.getActiveBookings();
    }

    @GetMapping("/conflict/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getConflictingBookings(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return bookingService.getConflictingBookings(resourceId, startTime, endTime);
    }

    @GetMapping("/availability/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> checkResourceAvailability(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        boolean available = bookingService.isResourceAvailable(resourceId, startTime, endTime);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/range/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getBookingsInTimeRange(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return bookingService.getBookingsInTimeRange(resourceId, startTime, endTime);
    }

    @GetMapping("/recurring")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getRecurringBookings() {
        return bookingService.getRecurringBookings();
    }

    @GetMapping("/priority/{priorityLevel}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getBookingsByPriority(@PathVariable ResourceBookingSchedule.PriorityLevel priorityLevel) {
        return bookingService.getBookingsByPriorityLevel(priorityLevel);
    }

    @GetMapping("/payment-status/{paymentStatus}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getBookingsByPaymentStatus(@PathVariable ResourceBookingSchedule.PaymentStatus paymentStatus) {
        return bookingService.getBookingsByPaymentStatus(paymentStatus);
    }

    @GetMapping("/approved-by/{approvedBy}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getBookingsApprovedBy(@PathVariable String approvedBy) {
        return bookingService.getBookingsApprovedBy(approvedBy);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getOverduePendingBookings() {
        return bookingService.getOverduePendingBookings();
    }

    @GetMapping("/completed-without-attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getCompletedBookingsWithoutAttendance() {
        return bookingService.getCompletedBookingsWithoutAttendance();
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Object> getBookingStatistics() {
        long pendingCount = bookingService.countBookingsByStatus(ResourceBookingSchedule.BookingStatus.PENDING);
        long approvedCount = bookingService.countBookingsByStatus(ResourceBookingSchedule.BookingStatus.APPROVED);
        long cancelledCount = bookingService.countBookingsByStatus(ResourceBookingSchedule.BookingStatus.CANCELLED);
        long completedCount = bookingService.countBookingsByStatus(ResourceBookingSchedule.BookingStatus.COMPLETED);
        
        return ResponseEntity.ok(java.util.Map.of(
            "pending", pendingCount,
            "approved", approvedCount,
            "cancelled", cancelledCount,
            "completed", completedCount
        ));
    }

    @GetMapping("/date-range")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return bookingService.getBookingsByDateRange(startTime, endTime);
    }

    @GetMapping("/utilization/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getUtilizationData(
            @PathVariable Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return bookingService.getUtilizationData(resourceId, startTime, endTime);
    }

    @GetMapping("/revenue/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<Double> getTotalRevenueForResource(@PathVariable Long resourceId) {
        Double revenue = bookingService.calculateTotalRevenueForResource(resourceId);
        return ResponseEntity.ok(revenue != null ? revenue : 0.0);
    }

    @PatchMapping("/{id}/payment-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> request) {
        try {
            ResourceBookingSchedule.PaymentStatus paymentStatus = ResourceBookingSchedule.PaymentStatus.valueOf(request.get("paymentStatus"));
            ResourceBookingSchedule booking = bookingService.updatePaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/total-cost")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<?> updateTotalCost(@PathVariable Long id, @RequestBody java.util.Map<String, Object> request) {
        try {
            Double totalCost = request.get("totalCost") != null ? 
                Double.valueOf(request.get("totalCost").toString()) : null;
            ResourceBookingSchedule booking = bookingService.updateTotalCost(id, totalCost);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/process-overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> processOverdueBookings() {
        bookingService.processOverdueBookings();
        return ResponseEntity.ok("Overdue bookings processed successfully");
    }

    @PostMapping("/update-attendance")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public ResponseEntity<String> updateActualAttendance() {
        bookingService.updateActualAttendance();
        return ResponseEntity.ok("Actual attendance updated successfully");
    }

    @GetMapping("/large-bookings/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public List<ResourceBookingSchedule> getLargeBookingsForResource(@PathVariable Long resourceId, @RequestParam Integer minAttendees) {
        return bookingService.getLargeBookingsForResource(resourceId, minAttendees);
    }

    @GetMapping("/special-requirements/{resourceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<ResourceBookingSchedule> getBookingsWithSpecialRequirements(@PathVariable Long resourceId) {
        return bookingService.getBookingsWithSpecialRequirements(resourceId);
    }
}
