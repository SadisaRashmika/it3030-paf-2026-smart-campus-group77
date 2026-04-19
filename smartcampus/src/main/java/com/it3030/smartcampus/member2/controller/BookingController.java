package com.it3030.smartcampus.member2.controller;

import com.it3030.smartcampus.member2.dto.ApproveRejectRequest;
import com.it3030.smartcampus.member2.dto.BookingResponse;
import com.it3030.smartcampus.member2.dto.CreateBookingRequest;
import com.it3030.smartcampus.member2.exception.BookingConflictException;
import com.it3030.smartcampus.member2.service.BookingService;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/member2/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'ADMIN')")
    public ResponseEntity<?> createBooking(@RequestBody CreateBookingRequest request) {
        try {
            UserAccount currentUser = getCurrentUser();
            BookingResponse response = bookingService.createBooking(request, currentUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (BookingConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public List<BookingResponse> getMyBookings() {
        return bookingService.getMyBookings(getCurrentUser());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('TIMETABLE_MANAGER', 'ADMIN')")
    public List<BookingResponse> getPendingBookings() {
        return bookingService.getPendingBookings();
    }

    @GetMapping("/weekly")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'TIMETABLE_MANAGER', 'ADMIN', 'RESOURCE_ADMINISTATOR')")
    public List<BookingResponse> getWeeklyBookings(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return bookingService.getWeeklyApprovedBookings(start, end);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('TIMETABLE_MANAGER', 'ADMIN')")
    public ResponseEntity<?> approveBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.approveBooking(id));
        } catch (BookingConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('TIMETABLE_MANAGER', 'ADMIN')")
    public ResponseEntity<?> rejectBooking(@PathVariable Long id, @RequestBody ApproveRejectRequest request) {
        return ResponseEntity.ok(bookingService.rejectBooking(id, request));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.cancelBooking(id, getCurrentUser()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    private UserAccount getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            throw new IllegalStateException("Not authenticated");
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Current user not found in database"));
    }
}
