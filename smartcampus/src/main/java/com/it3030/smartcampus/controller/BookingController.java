package com.it3030.smartcampus.controller;

import com.it3030.smartcampus.dto.BookingRequestDTO;
import com.it3030.smartcampus.dto.BookingResponseDTO;
import com.it3030.smartcampus.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing the booking lifecycle API under {@code /api/bookings}.
 *
 * Endpoints:
 *  POST   /api/bookings                – user submits a booking request
 *  GET    /api/bookings                – admin: all bookings
 *  GET    /api/bookings/pending        – admin: pending queue
 *  GET    /api/bookings/my-bookings    – user: their own bookings (userId hardcoded until Auth is ready)
 *  GET    /api/bookings/{id}           – single booking by id
 *  PUT    /api/bookings/{id}/approve   – admin approves
 *  PUT    /api/bookings/{id}/reject    – admin rejects
 *  PUT    /api/bookings/{id}/cancel    – user/admin cancels
 *
 * Security annotations are commented out until Member 4 wires up Spring Security.
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*") // tighten in production
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    /**
     * User submits a new booking request.
     * Returns HTTP 201 Created on success, HTTP 409 Conflict if the slot is taken.
     */
    @PostMapping
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BookingResponseDTO> requestBooking(
            @RequestBody BookingRequestDTO bookingRequest) {
        BookingResponseDTO created = bookingService.createBooking(bookingRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    /**
     * Admin retrieves every booking in the system.
     */
    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAllBookings());
    }

    /**
     * Admin retrieves only PENDING bookings (the approval queue).
     */
    @GetMapping("/pending")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getPendingBookings() {
        return ResponseEntity.ok(bookingService.findPendingBookings());
    }

    /**
     * User retrieves their own bookings.
     * TODO: replace hardcoded userId with SecurityContextHolder lookup once Member 4 is done.
     */
    @GetMapping("/my-bookings")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(
            @RequestParam(required = false, defaultValue = "1") Long userId) {
        return ResponseEntity.ok(bookingService.findBookingsForUser(userId));
    }

    /**
     * Fetch any single booking by ID.
     */
    @GetMapping("/{id}")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.findById(id));
    }

    // -------------------------------------------------------------------------
    // ADMIN ACTIONS
    // -------------------------------------------------------------------------

    /**
     * Admin approves a pending booking.
     * Re-runs conflict check at approval time to handle concurrent submissions.
     */
    @PutMapping("/{id}/approve")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> approveBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.approveBooking(id));
    }

    /**
     * Admin rejects a pending booking with a reason.
     */
    @PutMapping("/{id}/reject")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingResponseDTO> rejectBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingService.rejectBooking(id, reason));
    }

    // -------------------------------------------------------------------------
    // USER / ADMIN ACTIONS
    // -------------------------------------------------------------------------

    /**
     * User or admin cancels a PENDING or APPROVED booking.
     */
    @PutMapping("/{id}/cancel")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
}
