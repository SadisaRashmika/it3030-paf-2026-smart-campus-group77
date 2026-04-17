package com.it3030.smartcampus.member2.dto;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
		@NotNull Long resourceId,
		@NotNull @Future Instant startTime,
		@NotNull @Future Instant endTime
) {}
