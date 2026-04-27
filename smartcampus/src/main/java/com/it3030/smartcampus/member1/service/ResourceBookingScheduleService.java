package com.it3030.smartcampus.member1.service;

import com.it3030.smartcampus.member1.model.ResourceBookingSchedule;
import com.it3030.smartcampus.member1.model.EnhancedResource;
import com.it3030.smartcampus.member1.repository.ResourceBookingScheduleRepository;
import com.it3030.smartcampus.member1.repository.EnhancedResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ResourceBookingScheduleService {

    @Autowired
    private ResourceBookingScheduleRepository bookingRepository;

    @Autowired
    private EnhancedResourceRepository resourceRepository;

    public List<ResourceBookingSchedule> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<ResourceBookingSchedule> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<ResourceBookingSchedule> getBookingsByResource(Long resourceId) {
        return bookingRepository.findBookingsByResourceId(resourceId);
    }

    public List<ResourceBookingSchedule> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public ResourceBookingSchedule createBooking(ResourceBookingSchedule booking) {
        // Validate resource exists and is available
        EnhancedResource resource = resourceRepository.findById(booking.getResource().getId())
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + booking.getResource().getId()));

        if (!resource.getAvailable() || resource.getMaintenanceStatus() != EnhancedResource.MaintenanceStatus.GOOD) {
            throw new IllegalStateException("Resource is not available for booking");
        }

        // Check for booking conflicts
        List<ResourceBookingSchedule> conflicts = bookingRepository.findConflictingBookings(
                resource.getId(), booking.getStartTime(), booking.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new IllegalStateException("Resource is already booked for the requested time slot");
        }

        // Validate booking times
        if (booking.getStartTime().isAfter(booking.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book for past times");
        }

        booking.setResource(resource);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        ResourceBookingSchedule savedBooking = bookingRepository.save(booking);

        // Update resource booking count (if the method exists)
        // resourceRepository.incrementBookingCount(booking.getResource().getId());

        return savedBooking;
    }

    public ResourceBookingSchedule updateBooking(Long id, ResourceBookingSchedule bookingDetails) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        // Check if booking can be updated (only pending bookings can be updated)
        if (booking.getStatus() != ResourceBookingSchedule.BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be updated");
        }

        // Validate new times don't conflict with existing bookings
        if (!booking.getStartTime().equals(bookingDetails.getStartTime()) || 
            !booking.getEndTime().equals(bookingDetails.getEndTime())) {
            
            List<ResourceBookingSchedule> conflicts = bookingRepository.findConflictingBookings(
                    booking.getResource().getId(), bookingDetails.getStartTime(), bookingDetails.getEndTime());

            // Remove current booking from conflicts check
            conflicts.removeIf(conflict -> conflict.getId().equals(id));

            if (!conflicts.isEmpty()) {
                throw new IllegalStateException("Resource is already booked for the requested time slot");
            }
        }

        booking.setBookingTitle(bookingDetails.getBookingTitle());
        booking.setBookingDescription(bookingDetails.getBookingDescription());
        booking.setStartTime(bookingDetails.getStartTime());
        booking.setEndTime(bookingDetails.getEndTime());
        booking.setExpectedAttendees(bookingDetails.getExpectedAttendees());
        booking.setPriorityLevel(bookingDetails.getPriorityLevel());
        booking.setSpecialRequirements(bookingDetails.getSpecialRequirements());
        booking.setRecurringBooking(bookingDetails.getRecurringBooking());
        booking.setRecurringPattern(bookingDetails.getRecurringPattern());
        booking.setRecurringEndDate(bookingDetails.getRecurringEndDate());
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        // Only allow deletion of pending bookings
        if (booking.getStatus() != ResourceBookingSchedule.BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be deleted");
        }

        bookingRepository.delete(booking);
    }

    public ResourceBookingSchedule approveBooking(Long id, String approvedBy, String approvalNotes) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() != ResourceBookingSchedule.BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be approved");
        }

        booking.setStatus(ResourceBookingSchedule.BookingStatus.APPROVED);
        booking.setApprovedBy(approvedBy);
        booking.setApprovalNotes(approvalNotes);
        booking.setApprovedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public ResourceBookingSchedule rejectBooking(Long id, String approvedBy, String rejectionReason) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() != ResourceBookingSchedule.BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be rejected");
        }

        booking.setStatus(ResourceBookingSchedule.BookingStatus.REJECTED);
        booking.setApprovedBy(approvedBy);
        booking.setRejectionReason(rejectionReason);
        booking.setApprovedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public ResourceBookingSchedule cancelBooking(Long id, String cancellationReason) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() == ResourceBookingSchedule.BookingStatus.CANCELLED ||
            booking.getStatus() == ResourceBookingSchedule.BookingStatus.COMPLETED) {
            throw new IllegalStateException("Booking cannot be cancelled");
        }

        booking.setStatus(ResourceBookingSchedule.BookingStatus.CANCELLED);
        booking.setApprovalNotes(cancellationReason);
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public ResourceBookingSchedule completeBooking(Long id, Integer actualAttendees) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() != ResourceBookingSchedule.BookingStatus.APPROVED) {
            throw new IllegalStateException("Only approved bookings can be completed");
        }

        if (booking.getEndTime().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Booking cannot be completed before end time");
        }

        booking.setStatus(ResourceBookingSchedule.BookingStatus.COMPLETED);
        booking.setActualAttendees(actualAttendees);
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public ResourceBookingSchedule markNoShow(Long id) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() != ResourceBookingSchedule.BookingStatus.APPROVED) {
            throw new IllegalStateException("Only approved bookings can be marked as no-show");
        }

        if (booking.getEndTime().plusHours(1).isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Booking can only be marked as no-show after end time + 1 hour");
        }

        booking.setStatus(ResourceBookingSchedule.BookingStatus.NO_SHOW);
        booking.setActualAttendees(0);
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public List<ResourceBookingSchedule> getBookingsByStatus(ResourceBookingSchedule.BookingStatus status) {
        return bookingRepository.findByStatusOrderByStartTime(status);
    }

    public List<ResourceBookingSchedule> getPendingBookings() {
        return bookingRepository.findByStatusOrderByStartTime(ResourceBookingSchedule.BookingStatus.PENDING);
    }

    public List<ResourceBookingSchedule> getUpcomingBookingsForResource(Long resourceId) {
        return bookingRepository.findUpcomingBookingsForResource(resourceId, LocalDateTime.now());
    }

    public List<ResourceBookingSchedule> getUpcomingBookingsForUser(String userId) {
        return bookingRepository.findUpcomingBookingsForUser(userId, LocalDateTime.now());
    }

    public List<ResourceBookingSchedule> getActiveBookings() {
        return bookingRepository.findActiveBookings(LocalDateTime.now());
    }

    public List<ResourceBookingSchedule> getConflictingBookings(Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findConflictingBookings(resourceId, startTime, endTime);
    }

    public List<ResourceBookingSchedule> getBookingsInTimeRange(Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findBookingsInTimeRange(resourceId, startTime, endTime);
    }

    public List<ResourceBookingSchedule> getRecurringBookings() {
        return bookingRepository.findRecurringBookings();
    }

    public List<ResourceBookingSchedule> getHighPriorityBookings() {
        return bookingRepository.findBookingsByPriorityLevel(ResourceBookingSchedule.PriorityLevel.HIGH);
    }

    public List<ResourceBookingSchedule> getUrgentBookings() {
        return bookingRepository.findBookingsByPriorityLevel(ResourceBookingSchedule.PriorityLevel.URGENT);
    }

    public List<ResourceBookingSchedule> getBookingsByPaymentStatus(ResourceBookingSchedule.PaymentStatus paymentStatus) {
        return bookingRepository.findByPaymentStatus(paymentStatus);
    }

    public List<ResourceBookingSchedule> getBookingsApprovedBy(String approvedBy) {
        return bookingRepository.findBookingsApprovedBy(approvedBy);
    }

    public List<ResourceBookingSchedule> getOverduePendingBookings() {
        return bookingRepository.findOverduePendingBookings(LocalDateTime.now());
    }

    public List<ResourceBookingSchedule> getCompletedBookingsWithoutAttendance() {
        return bookingRepository.findCompletedBookingsWithoutAttendance(LocalDateTime.now());
    }

    public long countBookingsByStatus(ResourceBookingSchedule.BookingStatus status) {
        return bookingRepository.countBookingsByStatus(status);
    }

    public long countApprovedBookingsForResource(Long resourceId) {
        return bookingRepository.countApprovedBookingsForResource(resourceId);
    }

    public long countApprovedBookingsForUser(String userId) {
        return bookingRepository.countApprovedBookingsForUser(userId);
    }

    public boolean isResourceAvailable(Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        List<ResourceBookingSchedule> conflicts = bookingRepository.findConflictingBookings(resourceId, startTime, endTime);
        return conflicts.isEmpty();
    }

    public List<ResourceBookingSchedule> getUtilizationData(Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findUtilizationData(resourceId, startTime, endTime);
    }

    public Double calculateTotalRevenueForResource(Long resourceId) {
        return bookingRepository.calculateTotalRevenueForResource(resourceId);
    }

    public List<ResourceBookingSchedule> getRecentBookingsForResource(Long resourceId, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.findRecentBookingsForResource(resourceId, startDate, endDate);
    }

    public List<ResourceBookingSchedule> getLargeBookingsForResource(Long resourceId, Integer minAttendees) {
        return bookingRepository.findLargeBookingsForResource(resourceId, minAttendees);
    }

    public List<ResourceBookingSchedule> getBookingsWithSpecialRequirements(Long resourceId) {
        return bookingRepository.findBookingsWithSpecialRequirements(resourceId);
    }

    public ResourceBookingSchedule updatePaymentStatus(Long id, ResourceBookingSchedule.PaymentStatus paymentStatus) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setPaymentStatus(paymentStatus);
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public ResourceBookingSchedule updateTotalCost(Long id, Double totalCost) {
        ResourceBookingSchedule booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setTotalCost(totalCost);
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    public List<ResourceBookingSchedule> getBookingsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return bookingRepository.findBookingsByDateRange(startTime, endTime);
    }

    public List<ResourceBookingSchedule> getBookingsByPriorityLevel(ResourceBookingSchedule.PriorityLevel priorityLevel) {
        return bookingRepository.findBookingsByPriorityLevel(priorityLevel);
    }

    public void processOverdueBookings() {
        List<ResourceBookingSchedule> overdueBookings = getOverduePendingBookings();
        
        for (ResourceBookingSchedule booking : overdueBookings) {
            // Auto-reject bookings that are more than 24 hours old
            if (booking.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
                rejectBooking(booking.getId(), "SYSTEM", "Automatically rejected due to timeout");
            }
        }
    }

    public void updateActualAttendance() {
        List<ResourceBookingSchedule> completedWithoutAttendance = getCompletedBookingsWithoutAttendance();
        
        for (ResourceBookingSchedule booking : completedWithoutAttendance) {
            // Mark as no-show if no attendance recorded within 24 hours of completion
            if (booking.getEndTime().plusHours(24).isBefore(LocalDateTime.now())) {
                markNoShow(booking.getId());
            }
        }
    }
}
