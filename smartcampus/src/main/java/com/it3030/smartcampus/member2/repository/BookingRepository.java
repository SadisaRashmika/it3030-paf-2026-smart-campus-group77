package com.it3030.smartcampus.member2.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.it3030.smartcampus.member2.model.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	List<Booking> findByUserId(Integer userId);

	List<Booking> findByResourceId(Long resourceId);

	@Query("SELECT b FROM Booking b WHERE b.resource.id = :resourceId AND b.status IN ('PENDING', 'APPROVED') " +
			"AND ((b.startTime < :endTime AND b.endTime > :startTime))")
	List<Booking> findOverlappingBookings(@Param("resourceId") Long resourceId,
										  @Param("startTime") Instant startTime,
										  @Param("endTime") Instant endTime);
}
