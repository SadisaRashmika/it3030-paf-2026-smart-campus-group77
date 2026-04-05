package com.it3030.smartcampus.member4.service;

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

	public ConsoleMailService(ObjectProvider<JavaMailSender> mailSenderProvider,
							  @Value("${app.mail.enabled:true}") boolean mailEnabled,
							  @Value("${app.mail.from:no-reply@smartcampus.local}") String fromAddress) {
		this.mailSender = mailSenderProvider.getIfAvailable();
		this.mailEnabled = mailEnabled;
		this.fromAddress = fromAddress;
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
}