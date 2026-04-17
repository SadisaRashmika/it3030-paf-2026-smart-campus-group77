package com.it3030.smartcampus.service;

import com.it3030.smartcampus.dto.BookingRequestDTO;
import com.it3030.smartcampus.dto.BookingResponseDTO;
import com.it3030.smartcampus.exception.BookingConflictException;
import com.it3030.smartcampus.exception.ResourceNotFoundException;
import com.it3030.smartcampus.model.Booking;
import com.it3030.smartcampus.model.BookingStatus;
import com.it3030.smartcampus.model.Resource;
import com.it3030.smartcampus.model.User;
import com.it3030.smartcampus.repository.BookingRepository;
import com.it3030.smartcampus.repository.ResourceRepository;
import com.it3030.smartcampus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic layer for the resource-booking lifecycle.
 *
 * Key invariants enforced here:
 *  1. startTime must be before endTime.
 *  2. A new booking must not overlap with any APPROVED booking for the same resource.
 *  3. Status transitions follow: PENDING → APPROVED | REJECTED | CANCELLED.
 */
@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository,
                          ResourceRepository resourceRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    /**
     * Create a new booking request.
     *
     * <ol>
     *   <li>Validate the time window (startTime must be before endTime).</li>
     *   <li>Load the referenced Resource and User from the database.</li>
     *   <li>Run the overlap query – reject with HTTP 409 if a conflict is found.</li>
     *   <li>Persist the booking with status PENDING and return the response DTO.</li>
     * </ol>
     */
    public BookingResponseDTO createBooking(BookingRequestDTO dto) {
        // 1. Basic time validation
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new IllegalArgumentException("startTime and endTime are required.");
        }
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IllegalArgumentException("startTime must be before endTime.");
        }

        // 2. Resolve referenced entities
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found with id: " + dto.getResourceId()));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + dto.getUserId()));

        // 3. Conflict check – only APPROVED bookings block the slot
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                resource.getId(),
                dto.getStartTime(),
                dto.getEndTime());

        if (!overlaps.isEmpty()) {
            throw new BookingConflictException(
                    "Resource '" + resource.getName() + "' is already booked for the requested time slot. " +
                    "Conflicting booking ID: " + overlaps.get(0).getId());
        }

        // 4. Build and persist
        Booking booking = new Booking();
        booking.setResource(resource);
        booking.setUser(user);
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setPurpose(dto.getPurpose());
        booking.setExpectedAttendees(dto.getExpectedAttendees());
        booking.setStatus(BookingStatus.PENDING);

        return BookingResponseDTO.from(bookingRepository.save(booking));
    }

    // -------------------------------------------------------------------------
    // ADMIN ACTIONS
    // -------------------------------------------------------------------------

    /**
     * Admin approves a PENDING booking.
     * Before approving, re-runs the overlap check because another booking for
     * the same slot may have been approved in the meantime.
     */
    public BookingResponseDTO approveBooking(Long bookingId) {
        Booking booking = findOrThrow(bookingId);

        // Re-check for conflicts now that we are about to approve
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                booking.getResource().getId(),
                booking.getStartTime(),
                booking.getEndTime());

        if (!overlaps.isEmpty()) {
            throw new BookingConflictException(
                    "Cannot approve booking " + bookingId +
                    " – the time slot has already been approved for booking ID: " +
                    overlaps.get(0).getId());
        }

        booking.setStatus(BookingStatus.APPROVED);
        return BookingResponseDTO.from(bookingRepository.save(booking));
    }

    /** Admin rejects a booking request. */
    public BookingResponseDTO rejectBooking(Long bookingId, String reason) {
        Booking booking = findOrThrow(bookingId);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setAdminNotes(reason);
        return BookingResponseDTO.from(bookingRepository.save(booking));
    }

    // -------------------------------------------------------------------------
    // USER / ADMIN ACTIONS
    // -------------------------------------------------------------------------

    /** User or admin cancels a booking (PENDING or APPROVED). */
    public BookingResponseDTO cancelBooking(Long bookingId) {
        Booking booking = findOrThrow(bookingId);
        if (booking.getStatus() == BookingStatus.REJECTED ||
            booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Booking " + bookingId + " cannot be cancelled (current status: " +
                    booking.getStatus() + ").");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return BookingResponseDTO.from(bookingRepository.save(booking));
    }

    // -------------------------------------------------------------------------
    // QUERIES
    // -------------------------------------------------------------------------

    /** All bookings for a specific user (newest first). */
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> findBookingsForUser(Long userId) {
        return bookingRepository.findByUserIdOrderByStartTimeDesc(userId)
                .stream()
                .map(BookingResponseDTO::from)
                .collect(Collectors.toList());
    }

    /** All bookings in the system – for the admin dashboard. */
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> findAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(BookingResponseDTO::from)
                .collect(Collectors.toList());
    }

    /** All bookings with PENDING status – shortcut for the admin pending queue. */
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> findPendingBookings() {
        return bookingRepository.findByStatusOrderByStartTimeAsc(BookingStatus.PENDING)
                .stream()
                .map(BookingResponseDTO::from)
                .collect(Collectors.toList());
    }

    /** Single booking by ID. */
    @Transactional(readOnly = true)
    public BookingResponseDTO findById(Long bookingId) {
        return BookingResponseDTO.from(findOrThrow(bookingId));
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private Booking findOrThrow(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: " + bookingId));
    }
}
