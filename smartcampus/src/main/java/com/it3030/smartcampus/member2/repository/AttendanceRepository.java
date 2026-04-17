package com.it3030.smartcampus.member2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.it3030.smartcampus.member2.model.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	List<Attendance> findByBookingId(Long bookingId);
	List<Attendance> findByStudentId(Integer studentId);
	Optional<Attendance> findByStudentIdAndBookingId(Integer studentId, Long bookingId);
	long countByBookingId(Long bookingId);
}
