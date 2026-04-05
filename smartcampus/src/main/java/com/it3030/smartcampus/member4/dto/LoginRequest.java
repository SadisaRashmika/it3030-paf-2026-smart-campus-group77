package com.it3030.smartcampus.member4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@Email @NotBlank String email,
		@NotBlank String password) {
}