package com.it3030.smartcampus.member4.dto;

import com.it3030.smartcampus.member4.model.Role;

public record UserSummaryResponse(
		Integer id,
		String name,
		String userId,
		String email,
		Role role,
		boolean active,
		boolean suspicious,
		int otpRequestCount,
		int failedOtpAttempts,
		String status) {
}