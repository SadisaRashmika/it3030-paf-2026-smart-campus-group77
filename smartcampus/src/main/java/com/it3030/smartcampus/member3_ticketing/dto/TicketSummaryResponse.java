package com.it3030.smartcampus.member3_ticketing.dto;

import java.time.Instant;

public record TicketSummaryResponse(
    Long id,
    String title,
    String category,
    String priority,
    String status,
    String resourceLocation,
    String reporterEmail,
    String assignedTechnicianEmail,
    int commentCount,
    int attachmentCount,
    Instant createdAt,
    Instant updatedAt
) {}
