package com.it3030.smartcampus.member4.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.it3030.smartcampus.member4.dto.ForgotPasswordRequest;
import com.it3030.smartcampus.member4.dto.ForgotPasswordResetRequest;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Service
public class PasswordResetService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private final long otpTtlMinutes;

	public PasswordResetService(UserRepository userRepository,
							PasswordEncoder passwordEncoder,
							MailService mailService,
							@Value("${app.activation.otp-ttl-minutes:10}") long otpTtlMinutes) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
		this.otpTtlMinutes = otpTtlMinutes;
	}

	public void sendOtp(ForgotPasswordRequest request) {
		String email = UserAccount.normalize(request.email());
		UserAccount user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found for this email"));

		if (!user.isActive()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is not active. Please complete activation first.");
		}

		String otp = generateOtp();
		user.requestOtp(otp, Instant.now().plus(Duration.ofMinutes(otpTtlMinutes)));
		userRepository.save(user);
		mailService.sendOtp(user.getEmail(), otp);
	}

	public void resetPassword(ForgotPasswordResetRequest request) {
		String email = UserAccount.normalize(request.email());
		UserAccount user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found for this email"));

		if (!user.isActive()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account is not active. Please complete activation first.");
		}

		if (!user.otpMatches(request.otp().trim())) {
			user.recordFailedOtp();
			userRepository.save(user);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
		}

		if (user.otpExpired(Instant.now())) {
			user.recordFailedOtp();
			user.clearOtp();
			userRepository.save(user);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired. Request a new code.");
		}

		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		user.clearOtp();
		userRepository.save(user);
	}

	private String generateOtp() {
		return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
	}
}
