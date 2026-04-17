package com.it3030.smartcampus.member4.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecoveryRequestSubmissionRequest(
		@NotBlank(message = "Student ID number is required") String userId,
		@NotBlank(message = "Student email is required") @Email(message = "Enter a valid student email") String studentEmail,
		@NotBlank(message = "Contact email is required") @Email(message = "Enter a valid contact email") String contactEmail,
		@NotBlank(message = "Please describe the problem") @Size(max = 1000, message = "Problem description is too long") String issueSummary,
		@NotBlank(message = "ID photo file name is required") String idPhotoFileName,
		@NotBlank(message = "ID photo content type is required") String idPhotoContentType,
		@NotBlank(message = "ID photo data is required") String idPhotoDataUrl) {
}
