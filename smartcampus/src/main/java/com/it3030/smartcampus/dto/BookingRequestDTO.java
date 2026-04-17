package com.it3030.smartcampus.dto;

import java.time.LocalDateTime;

/**
 * Inbound DTO for creating a new booking request.
 * The client sends resource/user IDs rather than full objects,
 * keeping the API surface clean.
 */
public class BookingRequestDTO {

    private Long resourceId;   // which resource to book
    private Long userId;       // who is making the request (until Auth is wired up)
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private Integer expectedAttendees;

    // ---- Getters & Setters ----

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public Integer getExpectedAttendees() { return expectedAttendees; }
    public void setExpectedAttendees(Integer expectedAttendees) { this.expectedAttendees = expectedAttendees; }
}
