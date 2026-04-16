package com.it3030.smartcampus.member4.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "account_recovery_requests")
public class RecoveryRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false, length = 20)
	private String userId;

	@Column(name = "student_email", nullable = false)
	private String studentEmail;

	@Column(name = "contact_email", nullable = false)
	private String contactEmail;

	@Column(name = "issue_summary", nullable = false, columnDefinition = "TEXT")
	private String issueSummary;

	@Column(name = "id_photo_file_name", nullable = false)
	private String idPhotoFileName;

	@Column(name = "id_photo_content_type", nullable = false)
	private String idPhotoContentType;

	@Lob
	@Column(name = "id_photo_data_url", nullable = false, columnDefinition = "TEXT")
	private String idPhotoDataUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RecoveryRequestStatus status;

	@Column(name = "reviewed_at")
	private Instant reviewedAt;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	protected RecoveryRequest() {
	}

	private RecoveryRequest(String userId,
						 String studentEmail,
						 String contactEmail,
						 String issueSummary,
						 String idPhotoFileName,
						 String idPhotoContentType,
						 String idPhotoDataUrl) {
		this.userId = userId;
		this.studentEmail = studentEmail;
		this.contactEmail = contactEmail;
		this.issueSummary = issueSummary;
		this.idPhotoFileName = idPhotoFileName;
		this.idPhotoContentType = idPhotoContentType;
		this.idPhotoDataUrl = idPhotoDataUrl;
		this.status = RecoveryRequestStatus.PENDING;
	}

	public static RecoveryRequest submit(String userId,
										String studentEmail,
										String contactEmail,
										String issueSummary,
										String idPhotoFileName,
										String idPhotoContentType,
										String idPhotoDataUrl) {
		return new RecoveryRequest(userId, studentEmail, contactEmail, issueSummary, idPhotoFileName, idPhotoContentType, idPhotoDataUrl);
	}

	public void approve() {
		this.status = RecoveryRequestStatus.APPROVED;
		this.reviewedAt = Instant.now();
	}

	public void reject() {
		this.status = RecoveryRequestStatus.REJECTED;
		this.reviewedAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public String getIssueSummary() {
		return issueSummary;
	}

	public String getIdPhotoFileName() {
		return idPhotoFileName;
	}

	public String getIdPhotoContentType() {
		return idPhotoContentType;
	}

	public String getIdPhotoDataUrl() {
		return idPhotoDataUrl;
	}

	public RecoveryRequestStatus getStatus() {
		return status;
	}

	public Instant getReviewedAt() {
		return reviewedAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
