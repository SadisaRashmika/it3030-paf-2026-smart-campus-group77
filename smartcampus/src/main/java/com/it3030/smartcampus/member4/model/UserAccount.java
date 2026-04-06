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
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;

	@Column(name = "user_id", nullable = false, unique = true, length = 20)
	private String userId;

	@Column(nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Column(name = "password", nullable = false)
	private String passwordHash;

	@Column(name = "is_active", nullable = false)
	private boolean active;

	@Column(nullable = false)
	private boolean suspicious;

	@Column(name = "otp_request_count", nullable = false)
	private int otpRequestCount;

	@Column(name = "failed_otp_attempts", nullable = false)
	private int failedOtpAttempts;

	@Column(name = "otp", length = 6)
	private String currentOtp;

	@Column(name = "otp_expires_at")
	private Instant otpExpiresAt;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	protected UserAccount() {
	}

	private UserAccount(String name, String userId, String email, Role role, String passwordHash, boolean active) {
		this.name = name;
		this.userId = userId;
		this.email = email;
		this.role = role;
		this.passwordHash = passwordHash;
		this.active = active;
		this.suspicious = false;
		this.otpRequestCount = 0;
		this.failedOtpAttempts = 0;
	}

	public static UserAccount candidate(String userId, String email, Role role) {
		return new UserAccount(defaultName(userId, email), normalizeUserId(userId), normalize(email), role, "PENDING", false);
	}

	public static UserAccount activeUser(String userId, String email, String passwordHash, Role role) {
		return new UserAccount(defaultName(userId, email), normalizeUserId(userId), normalize(email), role, passwordHash, true);
	}

	public static UserAccount adminSeed(String userId, String email, String passwordHash) {
		return activeUser(userId, email, passwordHash, Role.ADMIN);
	}

	public static UserAccount lecturerSeed(String userId, String email, String passwordHash) {
		return activeUser(userId, email, passwordHash, Role.LECTURER);
	}

	public static UserAccount studentSeed(String userId, String email, String passwordHash) {
		return activeUser(userId, email, passwordHash, Role.STUDENT);
	}

	public static String normalize(String email) {
		return email == null ? null : email.trim().toLowerCase();
	}

	public static String normalizeUserId(String userId) {
		return userId == null ? null : userId.trim().toUpperCase();
	}

	private static String defaultName(String userId, String email) {
		String source = email != null && email.contains("@") ? email.substring(0, email.indexOf('@')) : userId;
		if (source == null || source.isBlank()) {
			return "Unknown User";
		}

		String candidate = source.replaceAll("[._-]+", " ").trim();
		if (candidate.isBlank()) {
			return normalizeUserId(userId);
		}

		String[] parts = candidate.split("\\s+");
		StringBuilder builder = new StringBuilder();
		for (String part : parts) {
			if (part.isBlank()) {
				continue;
			}
			if (!builder.isEmpty()) {
				builder.append(' ');
			}
			builder.append(Character.toUpperCase(part.charAt(0)));
			if (part.length() > 1) {
				builder.append(part.substring(1).toLowerCase());
			}
		}
		return builder.isEmpty() ? normalizeUserId(userId) : builder.toString();
	}

	public void requestOtp(String otp, Instant expiresAt) {
		this.currentOtp = otp == null ? null : otp.trim();
		this.otpExpiresAt = expiresAt;
		this.otpRequestCount++;
	}

	public boolean otpMatches(String otp) {
		return currentOtp != null && currentOtp.equals(otp);
	}

	public boolean otpExpired(Instant now) {
		return otpExpiresAt != null && now.isAfter(otpExpiresAt);
	}

	public void activate(String passwordHash) {
		this.passwordHash = passwordHash;
		this.active = true;
		this.currentOtp = null;
		this.otpExpiresAt = null;
		this.failedOtpAttempts = 0;
		this.suspicious = false;
	}

	public void flagSuspicious() {
		this.suspicious = true;
	}

	public void clearSuspicious() {
		this.suspicious = false;
		this.failedOtpAttempts = 0;
	}

	public void recordFailedOtp() {
		this.failedOtpAttempts++;
	}

	public void deactivate() {
		this.active = false;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void clearOtp() {
		this.currentOtp = null;
		this.otpExpiresAt = null;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public Role getRole() {
		return role;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isSuspicious() {
		return suspicious;
	}

	public int getOtpRequestCount() {
		return otpRequestCount;
	}

	public int getFailedOtpAttempts() {
		return failedOtpAttempts;
	}

	public Instant getOtpExpiresAt() {
		return otpExpiresAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}