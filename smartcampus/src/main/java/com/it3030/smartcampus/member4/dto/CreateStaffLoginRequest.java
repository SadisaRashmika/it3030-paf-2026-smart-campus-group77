package com.it3030.smartcampus.member4.dto;

import com.it3030.smartcampus.member4.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStaffLoginRequest(
		@NotBlank String name,
		@Email @NotBlank String email,
		@NotNull Role role) {
}