package com.it3030.smartcampus.member1.facilities.assets.model;

/**
 * Enum representing the various states of a booking request.
 */
public enum BookingStatus {
    /** Request is submitted and awaiting review by a Timetable Manager. */
    PENDING,
    
    /** Request has been approved. The resource is now locked for this time slot. */
    APPROVED,
    
    /** Request has been rejected by an administrator. */
    REJECTED,
    
    /** An approved booking has been cancelled by the user. */
    CANCELLED
}
