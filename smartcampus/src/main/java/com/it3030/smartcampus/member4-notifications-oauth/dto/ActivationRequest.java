package com.it3030.smartcampus.member4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ActivationRequest(
		@NotBlank @Pattern(regexp = "^(STU|LEC)[0-9]{3}$", message = "userId must be like STU001 or LEC001") String userId,
		@Email @NotBlank String email) {
}