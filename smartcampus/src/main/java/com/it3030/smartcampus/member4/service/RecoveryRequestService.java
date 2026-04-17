package com.it3030.smartcampus.member4.service;

import java.util.List;
import java.time.Duration;
import java.time.Instant;
import java.security.SecureRandom;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.it3030.smartcampus.member4.dto.RecoveryRequestResponse;
import com.it3030.smartcampus.member4.dto.RecoveryRequestSubmissionRequest;
import com.it3030.smartcampus.member4.dto.RecoveryRequestSubmissionResponse;
import com.it3030.smartcampus.member4.model.RecoveryRequest;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.RecoveryRequestRepository;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Service
public class RecoveryRequestService {
	private static final Logger log = LoggerFactory.getLogger(RecoveryRequestService.class);

	private final RecoveryRequestRepository recoveryRequestRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	public RecoveryRequestService(RecoveryRequestRepository recoveryRequestRepository,
								 UserRepository userRepository,
								 PasswordEncoder passwordEncoder,
								 MailService mailService) {
		this.recoveryRequestRepository = recoveryRequestRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	@Transactional
	public RecoveryRequestSubmissionResponse submit(RecoveryRequestSubmissionRequest request) {
		RecoveryRequest recoveryRequest = RecoveryRequest.submit(
				normalizeUserId(request.userId()),
				normalizeEmail(request.studentEmail()),
				normalizeEmail(request.contactEmail()),
				request.issueSummary().trim(),
				request.idPhotoFileName().trim(),
				request.idPhotoContentType().trim(),
				request.idPhotoDataUrl().trim());

		recoveryRequestRepository.save(recoveryRequest);
		return new RecoveryRequestSubmissionResponse(recoveryRequest.getId(), "Your recovery request has been submitted for admin review.");
	}

	@Transactional(readOnly = true)
	public List<RecoveryRequestResponse> listAll() {
		return recoveryRequestRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public RecoveryRequestResponse approve(Long requestId) {
		RecoveryRequest recoveryRequest = getRequest(requestId);
		UserAccount user = resolveRecoveryUser(recoveryRequest)
				.orElseThrow(() -> new IllegalArgumentException(
						"Matching user account not found for this recovery request (userId="
								+ recoveryRequest.getUserId()
								+ ", studentEmail="
								+ recoveryRequest.getStudentEmail()
								+ ")"));

		String temporaryPassword = generateTemporaryPassword();
		Instant expiresAt = Instant.now().plus(Duration.ofDays(1));
		user.setTemporaryPassword(passwordEncoder.encode(temporaryPassword), expiresAt);
		userRepository.save(user);

		recoveryRequest.approve();
		recoveryRequestRepository.save(recoveryRequest);

		mailService.sendRecoveryRequestApprovalEmail(
				recoveryRequest.getContactEmail(),
				recoveryRequest.getUserId(),
				recoveryRequest.getStudentEmail(),
				temporaryPassword,
				expiresAt);

		return toResponse(recoveryRequest);
	}

	@Transactional
	public RecoveryRequestResponse reject(Long requestId) {
		RecoveryRequest recoveryRequest = getRequest(requestId);
		recoveryRequest.reject();
		recoveryRequestRepository.save(recoveryRequest);

		mailService.sendRecoveryRequestRejectionEmail(
				recoveryRequest.getContactEmail(),
				recoveryRequest.getUserId(),
				recoveryRequest.getStudentEmail());

		return toResponse(recoveryRequest);
	}

	private RecoveryRequest getRequest(Long requestId) {
		return recoveryRequestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("Recovery request not found"));
	}

	private RecoveryRequestResponse toResponse(RecoveryRequest recoveryRequest) {
		UserAccount user = resolveRecoveryUser(recoveryRequest)
				.orElse(null);

		return new RecoveryRequestResponse(
				recoveryRequest.getId(),
				recoveryRequest.getUserId(),
				recoveryRequest.getStudentEmail(),
				recoveryRequest.getContactEmail(),
				recoveryRequest.getIssueSummary(),
				recoveryRequest.getIdPhotoFileName(),
				recoveryRequest.getIdPhotoContentType(),
				recoveryRequest.getIdPhotoDataUrl(),
				recoveryRequest.getStatus(),
				user == null ? null : user.getName(),
				user == null ? null : user.getEmail(),
				user == null ? null : user.getUserId(),
				user == null ? null : user.getRole().name(),
				user == null ? null : user.isActive(),
				recoveryRequest.getReviewedAt(),
				recoveryRequest.getCreatedAt());
	}

	private String normalizeEmail(String value) {
		return UserAccount.normalize(value);
	}

	private String normalizeUserId(String value) {
		return UserAccount.normalizeUserId(value);
	}

	private Optional<UserAccount> resolveRecoveryUser(RecoveryRequest recoveryRequest) {
		String requestUserId = normalizeUserId(recoveryRequest.getUserId());
		String requestEmail = normalizeEmail(recoveryRequest.getStudentEmail());

		Optional<UserAccount> byUserId = userRepository.findByUserId(requestUserId);
		if (byUserId.isPresent()) {
			return byUserId;
		}

		Optional<UserAccount> byEmail = userRepository.findByEmail(requestEmail);
		if (byEmail.isPresent()) {
			log.warn("Recovery request userId {} did not match a user. Falling back to email {}", requestUserId, requestEmail);
			return byEmail;
		}

		return Optional.empty();
	}

	private String generateTemporaryPassword() {
		final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
		StringBuilder builder = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			builder.append(alphabet.charAt(SECURE_RANDOM.nextInt(alphabet.length())));
		}
		return builder.toString();
	}
}
