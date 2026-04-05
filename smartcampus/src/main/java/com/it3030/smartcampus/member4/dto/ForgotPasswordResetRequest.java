package com.it3030.smartcampus.member4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordResetRequest(
		@Email @NotBlank String email,
		@NotBlank String otp,
		@NotBlank String newPassword) {
}
