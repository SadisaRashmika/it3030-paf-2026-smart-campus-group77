package com.it3030.smartcampus.member2.exception;

/**
 * Exception thrown when a booking request conflicts with an existing approved booking.
 */
public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}
