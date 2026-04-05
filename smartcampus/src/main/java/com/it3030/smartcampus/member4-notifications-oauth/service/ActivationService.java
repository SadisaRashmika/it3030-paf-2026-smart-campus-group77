package com.it3030.smartcampus.member4.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.it3030.smartcampus.member4.dto.ActivationRequest;
import com.it3030.smartcampus.member4.dto.ActivationResponse;
import com.it3030.smartcampus.member4.dto.OtpVerificationRequest;
import com.it3030.smartcampus.member4.dto.UserSummaryResponse;
import com.it3030.smartcampus.member4.model.Role;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Service
public class ActivationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private final int suspiciousThreshold;
	private final long otpTtlMinutes;

	public ActivationService(UserRepository userRepository,
							 PasswordEncoder passwordEncoder,
							 MailService mailService,
							 @Value("${app.activation.max-otp-requests-before-flag:3}") int suspiciousThreshold,
							 @Value("${app.activation.otp-ttl-minutes:10}") long otpTtlMinutes) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
		this.suspiciousThreshold = suspiciousThreshold;
		this.otpTtlMinutes = otpTtlMinutes;
	}

	public ActivationResponse requestOtp(ActivationRequest request) {
		String email = UserAccount.normalize(request.email());
		String userId = UserAccount.normalizeUserId(request.userId());
		UserAccount user = userRepository.findByUserIdAndEmail(userId, email)
				.orElseThrow(() -> new IllegalArgumentException("No account found for provided user ID and campus email"));

		if (user.getRole() == Role.ADMIN) {
			throw new IllegalArgumentException("Admin account does not require activation");
		}

		if (user.isActive()) {
			return new ActivationResponse("Account already active. Please login.", true, user.getOtpRequestCount(), user.isSuspicious());
		}

		String otp = generateOtp();
		user.requestOtp(otp, Instant.now().plus(Duration.ofMinutes(otpTtlMinutes)));
		if (!user.isActive() && user.getOtpRequestCount() >= suspiciousThreshold) {
			user.flagSuspicious();
		}

		userRepository.save(user);
		mailService.sendOtp(user.getEmail(), otp);
		return new ActivationResponse("OTP sent to email", false, user.getOtpRequestCount(), user.isSuspicious());
	}

	public ActivationResponse verifyOtpAndCreatePassword(OtpVerificationRequest request) {
		String email = UserAccount.normalize(request.email());
		String userId = UserAccount.normalizeUserId(request.userId());
		UserAccount user = userRepository.findByUserIdAndEmail(userId, email)
				.orElseThrow(() -> new IllegalArgumentException("No pending account found for provided user ID and email"));

		if (user.isActive()) {
			return new ActivationResponse("Account is already active", true, user.getOtpRequestCount(), user.isSuspicious());
		}

		if (!user.otpMatches(request.otp())) {
			user.recordFailedOtp();
			if (user.getFailedOtpAttempts() >= suspiciousThreshold) {
				user.flagSuspicious();
			}
			userRepository.save(user);
			return new ActivationResponse("Invalid OTP", false, user.getOtpRequestCount(), user.isSuspicious());
		}

		if (user.otpExpired(Instant.now())) {
			user.recordFailedOtp();
			user.flagSuspicious();
			user.clearOtp();
			userRepository.save(user);
			return new ActivationResponse("OTP expired. Request a new code.", false, user.getOtpRequestCount(), true);
		}

		user.activate(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);
		return new ActivationResponse("Account activated", true, user.getOtpRequestCount(), user.isSuspicious());
	}

	public List<UserSummaryResponse> listUsers() {
		return userRepository.findAll().stream().map(this::toSummary).toList();
	}

	public List<UserSummaryResponse> listSuspiciousUsers() {
		return userRepository.findBySuspiciousTrueOrderByEmailAsc().stream()
				.map(this::toSummary)
				.toList();
	}

	public UserSummaryResponse getStatus(String userId) {
		UserAccount user = userRepository.findByUserId(UserAccount.normalizeUserId(userId))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		return toSummary(user);
	}

	public UserSummaryResponse updateRole(Long userId, Role role) {
		UserAccount user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setRole(role);
		userRepository.save(user);
		return toSummary(user);
	}

	public UserSummaryResponse deactivate(Long userId) {
		UserAccount user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.deactivate();
		userRepository.save(user);
		return toSummary(user);
	}

	public void delete(Long userId) {
		userRepository.deleteById(userId);
	}

	private UserSummaryResponse toSummary(UserAccount user) {
		String status = user.isActive() ? "ACTIVE" : "PENDING_ACTIVATION";
		if (user.isSuspicious()) {
			status = status + "_SUSPICIOUS";
		}
		return new UserSummaryResponse(
				user.getId(),
				user.getUserId(),
				user.getEmail(),
				user.getRole(),
				user.isActive(),
				user.isSuspicious(),
				user.getOtpRequestCount(),
				user.getFailedOtpAttempts(),
				status);
	}

	private String generateOtp() {
		return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
	}
}