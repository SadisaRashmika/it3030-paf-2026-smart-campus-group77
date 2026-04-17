package com.it3030.smartcampus.member4.dto;

import jakarta.validation.constraints.NotBlank;

public record ProfilePictureUpdateRequest(
		@NotBlank(message = "Profile picture data is required") String profilePictureDataUrl) {
}