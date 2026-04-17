package com.it3030.smartcampus.member2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.it3030.smartcampus.member2.dto.BookingRequest;
import com.it3030.smartcampus.member2.model.Booking;
import com.it3030.smartcampus.member2.model.Booking.BookingStatus;
import com.it3030.smartcampus.member2.service.BookingService;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/member2/bookings")
public class BookingController {

	private final BookingService bookingService;
	private final UserRepository userRepository;

	public BookingController(BookingService bookingService, UserRepository userRepository) {
		this.bookingService = bookingService;
		this.userRepository = userRepository;
	}

	@PostMapping
	@PreAuthorize("hasRole('LECTURER')")
	public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest request, Authentication authentication) {
		UserAccount user = getCurrentUser(authentication);
		return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(user, request));
	}

	@PostMapping("/{id}/join")
	@PreAuthorize("hasRole('STUDENT')")
	public ResponseEntity<Void> joinSession(@PathVariable Long id, Authentication authentication) {
		UserAccount student = getCurrentUser(authentication);
		bookingService.joinSession(student, id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/my")
	public List<Booking> getMyBookings(Authentication authentication) {
		UserAccount user = getCurrentUser(authentication);
		return bookingService.getMyBookings(user);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER', 'STUDENT')")
	public List<Booking> getAllBookings() {
		return bookingService.getAllBookings();
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER')")
	public Booking updateStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
		return bookingService.updateStatus(id, status);
	}

	@GetMapping("/today/mine")
	public List<Booking> getTodayMyBookings(Authentication authentication) {
		UserAccount user = getCurrentUser(authentication);
		return bookingService.getTodayBookingsForUser(user);
	}

	@GetMapping("/stats")
	@PreAuthorize("hasAnyRole('ADMIN', 'TIMETABLE_MANAGER')")
	public java.util.Map<String, Object> getBookingStats() {
		return bookingService.getStats();
	}

	@DeleteMapping("/{id}")

	public ResponseEntity<Void> cancelBooking(@PathVariable Long id, Authentication authentication) {
		UserAccount user = getCurrentUser(authentication);
		bookingService.cancelBooking(id, user);
		return ResponseEntity.noContent().build();
	}

	private UserAccount getCurrentUser(Authentication authentication) {
		if (authentication == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
		}
		return userRepository.findByEmail(authentication.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
	}
}
