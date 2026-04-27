package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.ResourceBookingSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceBookingScheduleRepository extends JpaRepository<ResourceBookingSchedule, Long> {

    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId")
    List<ResourceBookingSchedule> findBookingsByResourceId(@Param("resourceId") Long resourceId);
    
    List<ResourceBookingSchedule> findByUserId(String userId);
    
    // Removed problematic method - use custom query instead
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "((b.startTime < :endTime AND b.endTime > :startTime) OR " +
           "(b.startTime >= :startTime AND b.startTime < :endTime))")
    List<ResourceBookingSchedule> findConflictingBookings(
        @Param("resourceId") Long resourceId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "b.startTime >= :startTime AND b.endTime <= :endTime AND b.status != 'CANCELLED'")
    List<ResourceBookingSchedule> findBookingsInTimeRange(
        @Param("resourceId") Long resourceId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.userId = :userId AND b.status = :status")
    List<ResourceBookingSchedule> findByUserIdAndStatus(
        @Param("userId") String userId, @Param("status") ResourceBookingSchedule.BookingStatus status);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.status = :status ORDER BY b.startTime ASC")
    List<ResourceBookingSchedule> findByStatusOrderByStartTime(
        @Param("status") ResourceBookingSchedule.BookingStatus status);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.startTime >= :startTime AND b.startTime <= :endTime")
    List<ResourceBookingSchedule> findBookingsByDateRange(
        @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND b.startTime >= :startTime")
    List<ResourceBookingSchedule> findUpcomingBookingsForResource(
        @Param("resourceId") Long resourceId, @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.userId = :userId AND b.startTime >= :startTime")
    List<ResourceBookingSchedule> findUpcomingBookingsForUser(
        @Param("userId") String userId, @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT COUNT(b) FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND b.status = 'APPROVED'")
    long countApprovedBookingsForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT COUNT(b) FROM ResourceBookingSchedule b WHERE b.userId = :userId AND b.status = 'APPROVED'")
    long countApprovedBookingsForUser(@Param("userId") String userId);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.recurringBooking = true")
    List<ResourceBookingSchedule> findRecurringBookings();
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.priority = :priorityLevel")
    List<ResourceBookingSchedule> findBookingsByPriorityLevel(@Param("priorityLevel") ResourceBookingSchedule.PriorityLevel priorityLevel);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.paymentStatus = :paymentStatus")
    List<ResourceBookingSchedule> findByPaymentStatus(
        @Param("paymentStatus") ResourceBookingSchedule.PaymentStatus paymentStatus);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.approvedBy = :approvedBy")
    List<ResourceBookingSchedule> findBookingsApprovedBy(@Param("approvedBy") String approvedBy);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.startTime < :now AND b.endTime > :now AND b.status = 'APPROVED'")
    List<ResourceBookingSchedule> findActiveBookings(@Param("now") LocalDateTime now);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.endTime < :now AND b.status = 'APPROVED' AND b.actualAttendees IS NULL")
    List<ResourceBookingSchedule> findCompletedBookingsWithoutAttendance(@Param("now") LocalDateTime now);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.startTime < :now AND b.status = 'PENDING'")
    List<ResourceBookingSchedule> findOverduePendingBookings(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(b) FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "b.startTime >= :startDate AND b.startTime <= :endDate AND b.status = 'APPROVED'")
    long countBookingsForResourceInPeriod(
        @Param("resourceId") Long resourceId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(b.totalCost) FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "b.status = 'COMPLETED' AND b.paymentStatus = 'PAID'")
    Double calculateTotalRevenueForResource(@Param("resourceId") Long resourceId);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "b.startTime >= :startDate AND b.startTime <= :endDate ORDER BY b.startTime DESC")
    List<ResourceBookingSchedule> findRecentBookingsForResource(
        @Param("resourceId") Long resourceId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "b.expectedAttendees >= :minAttendees ORDER BY b.expectedAttendees DESC")
    List<ResourceBookingSchedule> findLargeBookingsForResource(
        @Param("resourceId") Long resourceId, @Param("minAttendees") Integer minAttendees);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "b.specialRequirements IS NOT NULL AND b.specialRequirements != ''")
    List<ResourceBookingSchedule> findBookingsWithSpecialRequirements(@Param("resourceId") Long resourceId);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "b.startTime >= :startTime AND b.endTime <= :endTime AND b.status IN ('APPROVED', 'COMPLETED')")
    List<ResourceBookingSchedule> findUtilizationData(
        @Param("resourceId") Long resourceId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(b) FROM ResourceBookingSchedule b WHERE b.status = :status")
    long countBookingsByStatus(@Param("status") ResourceBookingSchedule.BookingStatus status);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.resource.id = :resourceId AND " +
           "b.startTime >= :startTime ORDER BY b.startTime ASC")
    Page<ResourceBookingSchedule> findUpcomingBookingsForResourcePaginated(
        @Param("resourceId") Long resourceId, @Param("startTime") LocalDateTime startTime, Pageable pageable);
    
    @Query("SELECT b FROM ResourceBookingSchedule b WHERE b.userId = :userId AND " +
           "b.startTime >= :startTime ORDER BY b.startTime ASC")
    Page<ResourceBookingSchedule> findUpcomingBookingsForUserPaginated(
        @Param("userId") String userId, @Param("startTime") LocalDateTime startTime, Pageable pageable);
}
