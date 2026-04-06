package com.it3030.smartcampus.member4.service;

import java.net.URLEncoder;
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
			log.error("Failed to send OTP email to {}", email, ex);
			throw new IllegalStateException("Unable to send OTP email right now. Please try again.");
		}
	}

	@Override
	public void sendStaffOnboardingEmail(String email, String name, String userId, String role, String otp) {
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
			log.error("Failed to send staff onboarding email to {}", email, ex);
			throw new IllegalStateException("Unable to send onboarding email right now. Please try again.");
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
}