package com.it3030.smartcampus.member4.dto;

public record AuthUserResponse(
		String email,
		String userId,
		String role,
		boolean authenticated) {
}