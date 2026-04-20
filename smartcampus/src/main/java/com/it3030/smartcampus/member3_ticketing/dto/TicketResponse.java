package com.it3030.smartcampus.member3_ticketing.dto;

import java.time.Instant;
import java.util.List;

public record TicketResponse(
    Long id,
    String title,
    String description,
    String category,
    String priority,
    String status,
    String resourceLocation,
    String contactEmail,
    String contactPhone,
    String reporterEmail,
    String assignedTechnicianEmail,
    String rejectionReason,
    String resolutionNotes,
    Instant createdAt,
    Instant updatedAt,
    List<AttachmentResponse> attachments,
    List<CommentResponse> comments
) {
    public record AttachmentResponse(Long id, String dataUrl, String fileName, Instant createdAt) {}
}
