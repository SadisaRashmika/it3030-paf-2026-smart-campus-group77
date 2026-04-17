package com.it3030.smartcampus.member4.service;

public interface MailService {

	void sendOtp(String email, String otp);

	void sendStaffOnboardingEmail(String email, String name, String userId, String role, String otp);

	void sendNotificationEmail(String email, String subject, String title, String message);

	void sendRecoveryRequestApprovalEmail(String email, String userId, String studentEmail, String temporaryPassword, java.time.Instant expiresAt);

	void sendRecoveryRequestRejectionEmail(String email, String userId, String studentEmail);
}