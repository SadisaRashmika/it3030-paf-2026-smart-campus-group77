package com.it3030.smartcampus.dto;

import com.it3030.smartcampus.model.Booking;
import com.it3030.smartcampus.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * Outbound DTO returned by every booking endpoint.
 * Flattens the nested Resource and User objects into plain fields
 * so the frontend never has to deal with circular references.
 */
public class BookingResponseDTO {

    private Long id;

    // Resource info
    private Long resourceId;
    private String resourceName;
    private String resourceLocation;
    private String resourceType;

    // User info
    private Long userId;
    private String userName;
    private String userEmail;

    // Booking details
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String purpose;
    private BookingStatus status;
    private Integer expectedAttendees;
    private String adminNotes;

    /** Factory method: build a DTO from a fully-loaded Booking entity. */
    public static BookingResponseDTO from(Booking b) {
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.id = b.getId();

        if (b.getResource() != null) {
            dto.resourceId       = b.getResource().getId();
            dto.resourceName     = b.getResource().getName();
            dto.resourceLocation = b.getResource().getLocation();
            dto.resourceType     = b.getResource().getType();
        }

        if (b.getUser() != null) {
            dto.userId    = b.getUser().getId();
            dto.userName  = b.getUser().getName();
            dto.userEmail = b.getUser().getEmail();
        }

        dto.startTime = b.getStartTime();
        dto.endTime   = b.getEndTime();
        dto.purpose   = b.getPurpose();
        dto.status    = b.getStatus();
        dto.expectedAttendees = b.getExpectedAttendees();
        dto.adminNotes = b.getAdminNotes();

        return dto;
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getResourceLocation() { return resourceLocation; }
    public void setResourceLocation(String resourceLocation) { this.resourceLocation = resourceLocation; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public Integer getExpectedAttendees() { return expectedAttendees; }
    public void setExpectedAttendees(Integer expectedAttendees) { this.expectedAttendees = expectedAttendees; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}
