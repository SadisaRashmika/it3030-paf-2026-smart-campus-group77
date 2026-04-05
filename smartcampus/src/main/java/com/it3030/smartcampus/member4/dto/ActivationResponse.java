package com.it3030.smartcampus.member4.dto;

public record ActivationResponse(
		String message,
		boolean activated,
		int otpRequestCount,
		boolean suspicious) {
}