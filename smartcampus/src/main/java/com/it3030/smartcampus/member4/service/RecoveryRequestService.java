package com.it3030.smartcampus.member4.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.it3030.smartcampus.member4.dto.RecoveryRequestResponse;
import com.it3030.smartcampus.member4.dto.RecoveryRequestSubmissionRequest;
import com.it3030.smartcampus.member4.dto.RecoveryRequestSubmissionResponse;
import com.it3030.smartcampus.member4.model.RecoveryRequest;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.RecoveryRequestRepository;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Service
public class RecoveryRequestService {

	private final RecoveryRequestRepository recoveryRequestRepository;
	private final UserRepository userRepository;
	private final MailService mailService;

	public RecoveryRequestService(RecoveryRequestRepository recoveryRequestRepository,
								 UserRepository userRepository,
								 MailService mailService) {
		this.recoveryRequestRepository = recoveryRequestRepository;
		this.userRepository = userRepository;
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
		UserAccount user = userRepository.findByUserIdAndEmail(recoveryRequest.getUserId(), recoveryRequest.getStudentEmail())
				.orElse(null);

		if (user != null) {
			user.activate();
			user.clearSuspicious();
			userRepository.save(user);
		}

		recoveryRequest.approve();
		recoveryRequestRepository.save(recoveryRequest);

		mailService.sendRecoveryRequestDecisionEmail(
				recoveryRequest.getContactEmail(),
				recoveryRequest.getUserId(),
				recoveryRequest.getStudentEmail(),
				true);

		return toResponse(recoveryRequest);
	}

	@Transactional
	public RecoveryRequestResponse reject(Long requestId) {
		RecoveryRequest recoveryRequest = getRequest(requestId);
		recoveryRequest.reject();
		recoveryRequestRepository.save(recoveryRequest);

		mailService.sendRecoveryRequestDecisionEmail(
				recoveryRequest.getContactEmail(),
				recoveryRequest.getUserId(),
				recoveryRequest.getStudentEmail(),
				false);

		return toResponse(recoveryRequest);
	}

	private RecoveryRequest getRequest(Long requestId) {
		return recoveryRequestRepository.findById(requestId)
				.orElseThrow(() -> new IllegalArgumentException("Recovery request not found"));
	}

	private RecoveryRequestResponse toResponse(RecoveryRequest recoveryRequest) {
		UserAccount user = userRepository.findByUserIdAndEmail(recoveryRequest.getUserId(), recoveryRequest.getStudentEmail())
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
}
