package com.it3030.smartcampus.member4.service;

import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ConsoleMailService implements MailService {

	private static final Logger log = LoggerFactory.getLogger(ConsoleMailService.class);
	private final JavaMailSender mailSender;
	private final boolean mailEnabled;
	private final String fromAddress;
	private final String apiBaseUrl;

	public ConsoleMailService(ObjectProvider<JavaMailSender> mailSenderProvider,
							  @Value("${app.mail.enabled:true}") boolean mailEnabled,
							  @Value("${app.mail.from:no-reply@smartcampus.local}") String fromAddress,
							  @Value("${app.api-base-url:http://localhost:8081}") String apiBaseUrl) {
		this.mailSender = mailSenderProvider.getIfAvailable();
		this.mailEnabled = mailEnabled;
		this.fromAddress = fromAddress;
		this.apiBaseUrl = apiBaseUrl;
	}

	@Override
	public void sendOtp(String email, String otp) {
		log.info("System generated OTP for {}: {}", email, otp);
		System.out.println("\n==========================================");
		System.out.println("📢 OTP FOR " + email + ": " + otp);
		System.out.println("==========================================\n");

		if (!mailEnabled || mailSender == null) {
			log.info("Mail disabled. OTP {} for {}", otp, email);
			return;
		}

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
			helper.setTo(email);
			helper.setFrom(fromAddress);
			helper.setSubject("SmartCampus OTP Verification");
			helper.setText(buildOtpBody(otp), true);
			mailSender.send(message);
			log.info("OTP email sent to {}", email);
		} catch (MessagingException | MailException ex) {
			log.warn("Failed to send OTP email to {} (Printed to console instead). Error: {}", email, ex.getMessage());
		}
	}

	@Override
	public void sendStaffOnboardingEmail(String email, String name, String userId, String role, String otp) {
		log.info("System generated Staff Onboarding OTP for {}: {}", email, otp);
		System.out.println("\n==========================================");
		System.out.println("📢 STAFF OTP FOR " + email + ": " + otp);
		System.out.println("==========================================\n");

		if (!mailEnabled || mailSender == null) {
			log.info("Mail disabled. Staff onboarding for {} ({}) with userId {} and OTP {}", email, role, userId, otp);
			return;
		}

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
			helper.setTo(email);
			helper.setFrom(fromAddress);
			helper.setSubject("SmartCampus Staff Account Created");
			helper.setText(buildStaffOnboardingBody(name, email, userId, role, otp), true);
			mailSender.send(message);
			log.info("Staff onboarding email sent to {}", email);
		} catch (MessagingException | MailException ex) {
			log.warn("Failed to send staff onboarding email to {} (Printed to console instead). Error: {}", email, ex.getMessage());
		}
	}

	@Override
	public void sendNotificationEmail(String email, String subject, String title, String message) {
		if (!mailEnabled || mailSender == null) {
			log.info("Mail disabled. Notification '{}' for {}", title, email);
			return;
		}

		try {
			MimeMessage mail = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail, false, "UTF-8");
			helper.setTo(email);
			helper.setFrom(fromAddress);
			helper.setSubject(subject);
			helper.setText(buildNotificationBody(title, message), true);
			mailSender.send(mail);
			log.info("Notification email sent to {}", email);
		} catch (MessagingException | MailException ex) {
			log.error("Failed to send notification email to {}", email, ex);
			throw new IllegalStateException("Unable to send notification email right now. Please try again.");
		}
	}

	@Override
	public void sendRecoveryRequestApprovalEmail(String email, String userId, String studentEmail, String temporaryPassword, Instant expiresAt) {
		if (!mailEnabled || mailSender == null) {
			log.info("Mail disabled. Recovery approval for {} ({}) with temporary password", userId, studentEmail);
			return;
		}

		try {
			MimeMessage mail = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail, false, "UTF-8");
			helper.setTo(email);
			helper.setFrom(fromAddress);
			helper.setSubject("SmartCampus Account Recovery Approved");
			helper.setText(buildRecoveryApprovedBody(userId, studentEmail, temporaryPassword, expiresAt), true);
			mailSender.send(mail);
			log.info("Recovery approval email sent to {} for {}", email, userId);
		} catch (MessagingException | MailException ex) {
			log.error("Failed to send recovery approval email to {}", email, ex);
			throw new IllegalStateException("Unable to send recovery email right now. Please try again.");
		}
	}

	@Override
	public void sendRecoveryRequestRejectionEmail(String email, String userId, String studentEmail) {
		if (!mailEnabled || mailSender == null) {
			log.info("Mail disabled. Recovery rejection for {} ({})", userId, studentEmail);
			return;
		}

		try {
			MimeMessage mail = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mail, false, "UTF-8");
			helper.setTo(email);
			helper.setFrom(fromAddress);
			helper.setSubject("SmartCampus Account Recovery Rejected");
			helper.setText(buildRecoveryRejectedBody(userId, studentEmail), true);
			mailSender.send(mail);
			log.info("Recovery rejection email sent to {} for {}", email, userId);
		} catch (MessagingException | MailException ex) {
			log.error("Failed to send recovery rejection email to {}", email, ex);
			throw new IllegalStateException("Unable to send recovery email right now. Please try again.");
		}
	}

	private String buildOtpBody(String otp) {
		return """
				<html>
				  <body style=\"font-family:Segoe UI,Arial,sans-serif;background:#f8fafc;color:#0f172a;padding:24px;\">
				    <div style=\"max-width:520px;margin:0 auto;background:#ffffff;border-radius:14px;padding:24px;border:1px solid #e2e8f0;\">
				      <h2 style=\"margin:0 0 12px;font-size:20px;color:#1d4ed8;\">SmartCampus Account Activation</h2>
				      <p style=\"margin:0 0 12px;font-size:14px;line-height:1.6;\">Use the OTP below to complete your account activation. This code expires soon.</p>
				      <p style=\"margin:18px 0;font-size:30px;font-weight:700;letter-spacing:6px;color:#111827;\">%s</p>
				      <p style=\"margin:0;font-size:12px;color:#64748b;\">If you did not request this, you can ignore this email.</p>
				    </div>
				  </body>
				</html>
				""".formatted(otp);
	}

	private String buildNotificationBody(String title, String message) {
		return """
				<html>
				  <body style=\"font-family:Segoe UI,Arial,sans-serif;background:#f8fafc;color:#0f172a;padding:24px;\">
				    <div style=\"max-width:520px;margin:0 auto;background:#ffffff;border-radius:14px;padding:24px;border:1px solid #e2e8f0;\">
				      <h2 style=\"margin:0 0 12px;font-size:20px;color:#1d4ed8;\">SmartCampus Notification</h2>
				      <p style=\"margin:0 0 8px;font-size:16px;font-weight:700;color:#0f172a;\">%s</p>
				      <p style=\"margin:0 0 12px;font-size:14px;line-height:1.6;color:#334155;\">%s</p>
				      <p style=\"margin:0;font-size:12px;color:#64748b;\">Please sign in to SmartCampus for full details.</p>
				    </div>
				  </body>
				</html>
				""".formatted(title, message);
	}

	private String buildStaffOnboardingBody(String name, String email, String userId, String role, String otp) {
		String reportUrl = apiBaseUrl
				+ "/api/public/activation/report-suspicious?userId="
				+ URLEncoder.encode(userId, StandardCharsets.UTF_8)
				+ "&email="
				+ URLEncoder.encode(email, StandardCharsets.UTF_8);

		return """
				<html>
				  <body style=\"font-family:Segoe UI,Arial,sans-serif;background:#f8fafc;color:#0f172a;padding:24px;\">
				    <div style=\"max-width:520px;margin:0 auto;background:#ffffff;border-radius:14px;padding:24px;border:1px solid #e2e8f0;\">
				      <h2 style=\"margin:0 0 12px;font-size:20px;color:#1d4ed8;\">Welcome to SmartCampus</h2>
				      <p style=\"margin:0 0 10px;font-size:14px;line-height:1.6;\">Hello %s, your %s account has been created by admin.</p>
				      <p style=\"margin:0 0 6px;font-size:14px;\"><strong>User ID:</strong> %s</p>
				      <p style=\"margin:0 0 12px;font-size:14px;\"><strong>Email:</strong> %s</p>
				      <p style=\"margin:0 0 8px;font-size:14px;\">Use this OTP in the Activate page:</p>
				      <p style=\"margin:12px 0 18px;font-size:30px;font-weight:700;letter-spacing:6px;color:#111827;\">%s</p>
				      <p style=\"margin:0 0 10px;font-size:13px;color:#475569;\">If you did not request this account, click below to report suspicious activity:</p>
				      <p style=\"margin:0 0 16px;\"><a href=\"%s\" style=\"display:inline-block;background:#b91c1c;color:#ffffff;text-decoration:none;padding:10px 14px;border-radius:8px;font-size:13px;font-weight:600;\">Report Suspicious Account</a></p>
				      <p style=\"margin:0;font-size:12px;color:#64748b;\">Open SmartCampus, click Activate, then enter your User ID, Email, OTP, and new password.</p>
				    </div>
				  </body>
				</html>
				""".formatted(name, role, userId, email, otp, reportUrl);
	}

	private String buildRecoveryApprovedBody(String userId, String studentEmail, String temporaryPassword, Instant expiresAt) {
		String expiryText = formatExpiry(expiresAt);
		return """
				<html>
				  <body style=\"font-family:Segoe UI,Arial,sans-serif;background:#f8fafc;color:#0f172a;padding:24px;\">
				    <div style=\"max-width:520px;margin:0 auto;background:#ffffff;border-radius:14px;padding:24px;border:1px solid #e2e8f0;\">
				      <h2 style=\"margin:0 0 12px;font-size:20px;color:#15803d;\">Account Recovery Approved</h2>
				      <p style=\"margin:0 0 12px;font-size:14px;line-height:1.6;\">Your recovery request for user ID <strong>%s</strong> has been approved.</p>
				      
				      <div style=\"background:#fef3c7;border-left:4px solid #f59e0b;padding:12px;margin:16px 0;border-radius:4px;\">
				        <p style=\"margin:0 0 8px;font-size:13px;font-weight:600;color:#92400e;\">TEMPORARY PASSWORD (VALID FOR 24 HOURS ONLY)</p>
				        <p style=\"margin:8px 0 0;font-size:30px;font-weight:700;letter-spacing:4px;color:#111827;font-family:monospace;\">%s</p>
				      </div>
				      
				      <p style=\"margin:0 0 12px;font-size:14px;line-height:1.6;\">Use this temporary password to sign in with your campus email <strong>%s</strong>:</p>
				      <ul style=\"margin:12px 0;padding-left:24px;font-size:14px;line-height:1.8;color:#334155;\">
				        <li>Open SmartCampus login page</li>
				        <li>Enter email: <strong>%s</strong></li>
				        <li>Enter temporary password above</li>
				        <li>You will be prompted to set a new permanent password</li>
				      </ul>
				      
				      <p style=\"margin:16px 0 12px;font-size:14px;font-weight:600;color:#b91c1c;\">⚠️ This password expires on <strong>%s</strong></p>
				      <p style=\"margin:0 0 12px;font-size:13px;color:#475569;\">After expiry, you will need to submit a new recovery request. Act quickly!</p>
				      
				      <p style=\"margin:0;font-size:12px;color:#64748b;\">If you still have trouble, contact the SmartCampus admin team.</p>
				    </div>
				  </body>
				</html>
				""".formatted(userId, temporaryPassword, studentEmail, studentEmail, expiryText);
	}

	private String buildRecoveryRejectedBody(String userId, String studentEmail) {
		return """
				<html>
				  <body style=\"font-family:Segoe UI,Arial,sans-serif;background:#f8fafc;color:#0f172a;padding:24px;\">
				    <div style=\"max-width:520px;margin:0 auto;background:#ffffff;border-radius:14px;padding:24px;border:1px solid #e2e8f0;\">
				      <h2 style=\"margin:0 0 12px;font-size:20px;color:#b91c1c;\">Account Recovery Rejected</h2>
				      <p style=\"margin:0 0 12px;font-size:14px;line-height:1.6;\">Your recovery request for user ID <strong>%s</strong> and email <strong>%s</strong> was rejected after admin review.</p>
				      <p style=\"margin:0 0 12px;font-size:14px;line-height:1.6;\">If you believe this is a mistake, please submit a new request with clearer supporting information or contact the admin team directly.</p>
				      <p style=\"margin:0;font-size:12px;color:#64748b;\">This is an automated message from SmartCampus.</p>
				    </div>
				  </body>
				</html>
				""".formatted(userId, studentEmail);
	}

	private String formatExpiry(Instant expiresAt) {
		if (expiresAt == null) {
			return "24 hours from approval";
		}

		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
				.withZone(ZoneId.systemDefault())
				.format(expiresAt);
	}
}