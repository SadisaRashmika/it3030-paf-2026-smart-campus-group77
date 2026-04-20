package com.it3030.smartcampus.member3_ticketing.dto;

import java.time.Instant;

public record CommentResponse(
    Long id,
    Long ticketId,
    String commenterEmail,
    String content,
    Instant createdAt,
    Instant updatedAt,
    boolean isOwner
) {}
