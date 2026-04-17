package com.it3030.smartcampus.member2.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.it3030.smartcampus.member2.model.TimetableEntry;

@Repository
public interface TimetableRepository extends JpaRepository<TimetableEntry, Long> {

	List<TimetableEntry> findByDayOfWeek(DayOfWeek dayOfWeek);

	List<TimetableEntry> findByResourceId(Long resourceId);

	@Query("SELECT t FROM TimetableEntry t WHERE t.resource.id = :resourceId " +
			"AND t.dayOfWeek = :dayOfWeek " +
			"AND t.startTime < :endTime AND t.endTime > :startTime")
	List<TimetableEntry> findOverlapping(
			@Param("resourceId") Long resourceId,
			@Param("dayOfWeek") DayOfWeek dayOfWeek,
			@Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime);

	@Query("SELECT t.resource.name, COUNT(t) as cnt FROM TimetableEntry t GROUP BY t.resource.name ORDER BY cnt DESC")
	List<Object[]> findMostBookedResource();

	long countByResourceId(Long resourceId);
}
