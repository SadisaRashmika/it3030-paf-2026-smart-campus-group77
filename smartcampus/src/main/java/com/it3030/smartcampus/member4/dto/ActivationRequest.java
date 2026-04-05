package com.it3030.smartcampus.member4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActivationRequest(
		@NotBlank String userId,
		@Email @NotBlank String email) {
}