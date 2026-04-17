package com.it3030.smartcampus.member4.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

public record AssignLecturerWorkRequest(
		@NotEmpty List<Integer> lecturerIds,
		@NotBlank String workTitle,
		@NotBlank String description,
		@NotBlank String location,
		LocalDate startDate,
		LocalDate endDate,
		LocalTime startTime,
		LocalTime endTime,
		boolean sendEmail) {
}