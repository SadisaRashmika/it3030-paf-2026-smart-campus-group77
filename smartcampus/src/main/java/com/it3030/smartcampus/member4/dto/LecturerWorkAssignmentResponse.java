package com.it3030.smartcampus.member4.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record LecturerWorkAssignmentResponse(
		Long assignmentId,
		List<Integer> lecturerIds,
		String workTitle,
		String description,
		String location,
		LocalDate startDate,
		LocalDate endDate,
		LocalTime startTime,
		LocalTime endTime,
		int notificationCount,
		int emailSentCount,
		String message) {
}