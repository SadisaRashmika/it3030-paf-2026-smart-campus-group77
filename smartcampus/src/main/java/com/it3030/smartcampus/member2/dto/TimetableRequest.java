package com.it3030.smartcampus.member2.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TimetableRequest(
		@NotNull Long resourceId,
		@NotNull DayOfWeek dayOfWeek,
		@NotNull LocalTime startTime,
		@NotNull LocalTime endTime,
		@NotBlank String title,
		String description
) {}
