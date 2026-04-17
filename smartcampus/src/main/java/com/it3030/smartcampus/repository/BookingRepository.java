package com.it3030.smartcampus.repository;

import com.it3030.smartcampus.model.Booking;
import com.it3030.smartcampus.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Booking} entities.
 *
 * The most important method is {@link #findOverlappingBookings}, which is the
 * backbone of the double-booking prevention logic in {@link BookingService}.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Return every booking belonging to a specific user, newest first.
     */
    List<Booking> findByUserIdOrderByStartTimeDesc(Long userId);

    /**
     * Return every booking for a specific resource, regardless of status.
     */
    List<Booking> findByResourceId(Long resourceId);

    /**
     * Return every booking for a resource that has been APPROVED and whose
     * time window overlaps with [{@code startTime}, {@code endTime}).
     *
     * Overlap condition (Allen's interval algebra):
     *   existing.startTime < requested.endTime
     *   AND existing.endTime   > requested.startTime
     *
     * Only APPROVED bookings are checked – pending/rejected/cancelled bookings
     * do NOT block a time slot.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.resource.id = :resourceId
              AND b.status = com.it3030.smartcampus.model.BookingStatus.APPROVED
              AND b.startTime < :endTime
              AND b.endTime   > :startTime
            """)
    List<Booking> findOverlappingBookings(
            @Param("resourceId") Long resourceId,
            @Param("startTime")  LocalDateTime startTime,
            @Param("endTime")    LocalDateTime endTime);

    /**
     * Return every booking with a given status (useful for admin dashboards).
     */
    List<Booking> findByStatusOrderByStartTimeAsc(BookingStatus status);
}
