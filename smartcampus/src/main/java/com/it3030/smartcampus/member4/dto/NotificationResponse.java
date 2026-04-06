package com.it3030.smartcampus.member4.dto;

import java.time.Instant;

public record NotificationResponse(
		Long id,
		String userId,
		String message,
		boolean read,
		Instant createdAt) {
}
