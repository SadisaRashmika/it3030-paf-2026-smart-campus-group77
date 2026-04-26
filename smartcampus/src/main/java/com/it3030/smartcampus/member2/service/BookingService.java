package com.it3030.smartcampus.member2.service;

import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member1.service.ResourceService;
import com.it3030.smartcampus.member2.dto.ApproveRejectRequest;
import com.it3030.smartcampus.member2.dto.BookingResponse;
import com.it3030.smartcampus.member2.dto.CreateBookingRequest;
import com.it3030.smartcampus.member2.exception.BookingConflictException;
import com.it3030.smartcampus.member2.model.Booking;
import com.it3030.smartcampus.member2.model.BookingStatus;
import com.it3030.smartcampus.member2.repository.BookingRepository;
import com.it3030.smartcampus.member4.model.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ResourceService resourceService;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request, UserAccount user) {
        // 1. Validate time range
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book in the past");
        }

        // 2. Check for conflicts (Only against APPROVED bookings)
        List<Booking> conflicts = bookingRepository.findOverlappingApprovedBookings(
                request.getResourceId(), request.getStartTime(), request.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new BookingConflictException("Resource is already booked for this time period.");
        }

        // 3. Save booking
        Resource resource = resourceService.getResourceById(request.getResourceId());

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setResource(resource);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setPurpose(request.getPurpose());
        booking.setExpectedAttendees(request.getExpectedAttendees());
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        return mapToResponse(saved);
    }

    public List<BookingResponse> getMyBookings(UserAccount user) {
        return bookingRepository.findByUserOrderByStartTimeDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getPendingBookings() {
        System.out.println("SERVICE: Fetching pending bookings...");
        return bookingRepository.findByStatusOrderByStartTimeAsc(BookingStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getWeeklyApprovedBookings(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findAllApprovedInWeek(start, end).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponse approveBooking(Long id) {
        Booking booking = getBookingById(id);

        // Re-check conflict at approval time (in case another was approved in between)
        List<Booking> conflicts = bookingRepository.findOverlappingApprovedBookings(
                booking.getResource().getId(), booking.getStartTime(), booking.getEndTime());

        if (!conflicts.isEmpty()) {
            throw new BookingConflictException(
                    "Cannot approve: Resource now has a conflict with another approved booking.");
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse rejectBooking(Long id, ApproveRejectRequest request) {
        Booking booking = getBookingById(id);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setRejectionReason(request.getRejectionReason());
        booking.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse cancelBooking(Long id, UserAccount user) {
        Booking booking = getBookingById(id);

        // Security check: Only owner can cancel
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You are not authorized to cancel this booking.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        return mapToResponse(bookingRepository.save(booking));
    }

    private Booking getBookingById(@NonNull Long id) {
        Long bookingId = Objects.requireNonNull(id, "id must not be null");
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with id: " + id));
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse res = new BookingResponse();
        res.setId(booking.getId());

        if (booking.getResource() != null) {
            res.setResourceId(booking.getResource().getId());
            res.setResourceName(booking.getResource().getName());
            res.setResourceType(booking.getResource().getType());
        } else {
            res.setResourceName("Unknown Resource");
        }

        if (booking.getUser() != null) {
            res.setUserName(booking.getUser().getName());
        } else {
            res.setUserName("Unknown User");
        }

        res.setStartTime(booking.getStartTime());
        res.setEndTime(booking.getEndTime());
        res.setStatus(booking.getStatus());
        res.setPurpose(booking.getPurpose());
        res.setExpectedAttendees(booking.getExpectedAttendees());
        res.setRejectionReason(booking.getRejectionReason());
        return res;
    }
}
