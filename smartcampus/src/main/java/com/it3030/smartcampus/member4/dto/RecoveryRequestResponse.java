package com.it3030.smartcampus.member4.dto;

import java.time.Instant;

import com.it3030.smartcampus.member4.model.RecoveryRequestStatus;

public record RecoveryRequestResponse(
		Long id,
		String userId,
		String studentEmail,
		String contactEmail,
		String issueSummary,
		String idPhotoFileName,
		String idPhotoContentType,
		String idPhotoDataUrl,
		RecoveryRequestStatus status,
		String matchedName,
		String matchedEmail,
		String matchedUserId,
		String matchedRole,
		Boolean matchedAccountActive,
		Instant reviewedAt,
		Instant createdAt) {
}
