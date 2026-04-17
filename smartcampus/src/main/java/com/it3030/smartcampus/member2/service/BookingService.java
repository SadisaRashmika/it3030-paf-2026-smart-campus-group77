package com.it3030.smartcampus.member2.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member1.service.ResourceService;
import com.it3030.smartcampus.member2.dto.BookingRequest;
import com.it3030.smartcampus.member2.exception.BookingConflictException;
import com.it3030.smartcampus.member2.model.Attendance;
import com.it3030.smartcampus.member2.model.Booking;
import com.it3030.smartcampus.member2.model.Booking.BookingStatus;
import com.it3030.smartcampus.member2.repository.AttendanceRepository;
import com.it3030.smartcampus.member2.repository.BookingRepository;
import com.it3030.smartcampus.member4.model.UserAccount;

@Service
@Transactional
public class BookingService {

	private final BookingRepository bookingRepository;
	private final ResourceService resourceService;
	private final AttendanceRepository attendanceRepository;
	private final TimetableService timetableService;

	public BookingService(BookingRepository bookingRepository,
						  ResourceService resourceService,
						  AttendanceRepository attendanceRepository,
						  TimetableService timetableService) {
		this.bookingRepository = bookingRepository;
		this.resourceService = resourceService;
		this.attendanceRepository = attendanceRepository;
		this.timetableService = timetableService;
	}

	public List<Booking> getMyBookings(UserAccount user) {
		return bookingRepository.findByUserId(user.getId());
	}

	public List<Booking> getAllBookings() {
		return bookingRepository.findAll();
	}

	public Booking getBookingById(Long id) {
		return bookingRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));
	}

	public Booking createBooking(UserAccount user, BookingRequest request) {
		if (user.getRole() != com.it3030.smartcampus.member4.model.Role.LECTURER) {
			throw new IllegalArgumentException("Only lecturers can create booking requests.");
		}

		Resource resource = resourceService.getResourceById(request.resourceId());

		// Check ad-hoc vs. ad-hoc overlap
		List<Booking> overlapping = bookingRepository.findOverlappingBookings(
				request.resourceId(), request.startTime(), request.endTime());
		if (!overlapping.isEmpty()) {
			throw new BookingConflictException("Time slot is unavailable. The resource is already booked for the selected time.");
		}

		// Cross-table check: ad-hoc booking vs. official timetable
		if (timetableService.hasConflictWithTimetable(request.resourceId(), request.startTime(), request.endTime())) {
			throw new BookingConflictException("Time slot is unavailable. A recurring class is scheduled during that time.");
		}

		Booking booking = new Booking();
		booking.setUser(user);
		booking.setResource(resource);
		booking.setStartTime(request.startTime());
		booking.setEndTime(request.endTime());
		booking.setStatus(BookingStatus.PENDING);

		return bookingRepository.save(booking);
	}

	public Booking updateStatus(Long bookingId, BookingStatus status) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));
		booking.setStatus(status);
		return bookingRepository.save(booking);
	}

	public void joinSession(UserAccount student, Long bookingId) {
		Booking booking = getBookingById(bookingId);
		
		if (booking.getStatus() != BookingStatus.APPROVED) {
			throw new IllegalArgumentException("Cannot join an unapproved session.");
		}

		long currentAttendance = attendanceRepository.countByBookingId(bookingId);
		if (currentAttendance >= booking.getResource().getCapacity()) {
			throw new IllegalArgumentException("Session is at full capacity.");
		}

		if (attendanceRepository.findByStudentIdAndBookingId(student.getId(), bookingId).isPresent()) {
			throw new IllegalArgumentException("You are already registered for this session.");
		}

		Attendance attendance = new Attendance(student, booking);
		attendanceRepository.save(attendance);
	}

	public void cancelBooking(Long bookingId, UserAccount user) {
		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new IllegalArgumentException("Booking not found"));

		// Only the owner or an admin can cancel
		if (!booking.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
			throw new IllegalArgumentException("Unauthorized to cancel this booking");
		}

		booking.setStatus(BookingStatus.CANCELLED);
		bookingRepository.save(booking);
	}

	public List<Booking> getTodayBookingsForUser(UserAccount user) {
		Instant startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
		Instant endOfDay   = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
		return bookingRepository.findByUserId(user.getId()).stream()
				.filter(b -> b.getStatus() == BookingStatus.APPROVED)
				.filter(b -> !b.getStartTime().isBefore(startOfDay) && b.getStartTime().isBefore(endOfDay))
				.toList();
	}

	public Map<String, Object> getStats() {
		List<Booking> all = bookingRepository.findAll();
		long pending  = all.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count();
		long approved = all.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).count();
		Map<String, Object> stats = new LinkedHashMap<>();
		stats.put("pendingBookings", pending);
		stats.put("approvedBookings", approved);
		return stats;
	}
}
