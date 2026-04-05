package com.it3030.smartcampus.member4.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		String identifier,
		String email,
		String userId,
		@NotBlank String password) {
}