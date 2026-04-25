package com.it3030.smartcampus.member1.facilities.assets.repository;

import com.it3030.smartcampus.member1.facilities.assets.model.Booking;
import com.it3030.smartcampus.member1.facilities.assets.model.BookingStatus;
import com.it3030.smartcampus.member4.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.resource LEFT JOIN FETCH b.user WHERE b.user = :user ORDER BY b.startTime DESC")
    List<Booking> findByUserOrderByStartTimeDesc(@Param("user") UserAccount user);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.resource LEFT JOIN FETCH b.user WHERE b.status = :status ORDER BY b.startTime ASC")
    List<Booking> findByStatusOrderByStartTimeAsc(
            @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.resource.id = :resourceId " +
            "AND b.status = 'APPROVED' " +
            "AND b.startTime < :endTime " +
            "AND b.endTime > :startTime")
    List<Booking> findOverlappingApprovedBookings(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.resource LEFT JOIN FETCH b.user " +
            "WHERE b.status = 'APPROVED' " +
            "AND b.startTime < :weekEnd " +
            "AND b.endTime > :weekStart " +
            "ORDER BY b.startTime ASC")
    List<Booking> findAllApprovedInWeek(
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd);
}
