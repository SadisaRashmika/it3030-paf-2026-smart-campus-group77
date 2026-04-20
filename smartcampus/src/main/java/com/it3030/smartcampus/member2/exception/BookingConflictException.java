package com.it3030.smartcampus.member2.exception;

/**
 * Exception thrown when a booking request conflicts with an existing approved booking.
 */
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}
