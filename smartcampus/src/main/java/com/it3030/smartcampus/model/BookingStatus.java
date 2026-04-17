package com.it3030.smartcampus.model;

/**
 * Lifecycle states for a campus resource booking.
 *
 * PENDING  – request submitted, awaiting admin review
 * APPROVED – admin confirmed the booking
 * REJECTED – admin denied the booking
 * CANCELLED– user withdrew the request (only for PENDING/APPROVED bookings)
 */
public enum BookingStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CANCELLED
}
