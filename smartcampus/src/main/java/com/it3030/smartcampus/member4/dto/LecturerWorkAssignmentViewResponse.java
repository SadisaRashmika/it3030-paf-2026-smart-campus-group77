package com.it3030.smartcampus.member4.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record LecturerWorkAssignmentViewResponse(
		Long assignmentId,
		String workTitle,
		String description,
		String location,
		LocalDate startDate,
		LocalDate endDate,
		LocalTime startTime,
		LocalTime endTime,
		boolean sendEmail,
		Instant createdAt,
		List<String> recipientNames,
		List<String> recipientIds) {
}
