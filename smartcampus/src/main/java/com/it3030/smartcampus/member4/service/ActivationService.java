package com.it3030.smartcampus.member4.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.it3030.smartcampus.member4.dto.ActivationRequest;
import com.it3030.smartcampus.member4.dto.ActivationResponse;
import com.it3030.smartcampus.member4.dto.AssignLecturerWorkRequest;
import com.it3030.smartcampus.member4.dto.CreateStaffLoginRequest;
import com.it3030.smartcampus.member4.dto.LecturerWorkAssignmentResponse;
import com.it3030.smartcampus.member4.dto.LecturerWorkAssignmentViewResponse;
import com.it3030.smartcampus.member4.dto.OtpVerificationRequest;
import com.it3030.smartcampus.member4.dto.UserSummaryResponse;
import com.it3030.smartcampus.member4.model.Notification;
import com.it3030.smartcampus.member4.model.LecturerWorkAssignment;
import com.it3030.smartcampus.member4.model.Role;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.NotificationRepository;
import com.it3030.smartcampus.member4.repository.LecturerWorkAssignmentRepository;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Service
public class ActivationService {
	private static final Logger log = LoggerFactory.getLogger(ActivationService.class);

	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;
	private final LecturerWorkAssignmentRepository lecturerWorkAssignmentRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private final int suspiciousThreshold;
	private final long otpTtlMinutes;

	public ActivationService(UserRepository userRepository,
							 NotificationRepository notificationRepository,
							 LecturerWorkAssignmentRepository lecturerWorkAssignmentRepository,
							 PasswordEncoder passwordEncoder,
							 MailService mailService,
							 @Value("${app.activation.max-otp-requests-before-flag:3}") int suspiciousThreshold,
							 @Value("${app.activation.otp-ttl-minutes:10}") long otpTtlMinutes) {
		this.userRepository = userRepository;
		this.notificationRepository = notificationRepository;
		this.lecturerWorkAssignmentRepository = lecturerWorkAssignmentRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
		this.suspiciousThreshold = suspiciousThreshold;
		this.otpTtlMinutes = otpTtlMinutes;
	}

	public UserSummaryResponse createStaffLogin(CreateStaffLoginRequest request) {
		if (request.role() == Role.ADMIN) {
			throw new IllegalArgumentException("Admin accounts cannot be created from this action");
		}

		String email = UserAccount.normalize(request.email());
		String userId = generateNextUserId(request.role());
		String name = request.name().trim();

		if (userRepository.findByUserId(userId).isPresent()) {
			throw new IllegalArgumentException("User ID already exists");
		}

		if (userRepository.findByEmail(email).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}

		UserAccount user = UserAccount.candidate(userId, email, request.role());
		user.setName(name);
		String otp = generateOtp();
		user.requestOtp(otp, Instant.now().plus(Duration.ofMinutes(otpTtlMinutes)));
		userRepository.save(user);
		try {
			mailService.sendStaffOnboardingEmail(user.getEmail(), user.getName(), user.getUserId(), user.getRole().name(), otp);
		} catch (RuntimeException ex) {
			log.warn("User {} created, but onboarding email could not be sent to {}", user.getUserId(), user.getEmail(), ex);
		}

		return toSummary(user);
	}

	public LecturerWorkAssignmentResponse assignLecturerWork(AssignLecturerWorkRequest request) {
		List<UserAccount> lecturers = request.lecturerIds().stream()
				.map(lecturerIdValue -> userRepository.findById(lecturerIdValue)
						.orElseThrow(() -> new IllegalArgumentException("Lecturer not found: " + lecturerIdValue)))
				.peek(lecturer -> {
					if (lecturer.getRole() != Role.LECTURER) {
						throw new IllegalArgumentException("Selected account is not a lecturer: " + lecturer.getUserId());
					}
				})
				.toList();

		String title = request.workTitle().trim();
		String description = request.description().trim();
		String location = request.location().trim();
		String schedule = formatSchedule(request.startDate(), request.endDate(), request.startTime(), request.endTime());
		String message = "Work assigned: " + title + " | " + location + " | " + schedule + " | " + description;
		LecturerWorkAssignment assignment = LecturerWorkAssignment.of(
				title,
				description,
				location,
				request.startDate(),
				request.endDate(),
				request.startTime(),
				request.endTime(),
				request.sendEmail(),
				Set.copyOf(lecturers));
		lecturerWorkAssignmentRepository.save(assignment);

		int notificationCount = 0;
		int emailSentCount = 0;
		for (UserAccount lecturer : lecturers) {
			notificationRepository.save(Notification.of(lecturer, message));
			notificationCount++;
			if (request.sendEmail()) {
				mailService.sendNotificationEmail(
						lecturer.getEmail(),
						"SmartCampus Lecturer Work Assignment",
						title,
						message);
				emailSentCount++;
			}
		}

		return new LecturerWorkAssignmentResponse(
				assignment.getId(),
				request.lecturerIds(),
				title,
				description,
				location,
				request.startDate(),
				request.endDate(),
				request.startTime(),
				request.endTime(),
				notificationCount,
				emailSentCount,
				"Work assigned successfully");
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

	public List<LecturerWorkAssignmentViewResponse> listLecturerAssignments() {
		return lecturerWorkAssignmentRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(assignment -> new LecturerWorkAssignmentViewResponse(
						assignment.getId(),
						assignment.getWorkTitle(),
						assignment.getDescription(),
						assignment.getLocation(),
						assignment.getStartDate(),
						assignment.getEndDate(),
						assignment.getStartTime(),
						assignment.getEndTime(),
						assignment.isSendEmail(),
						assignment.getCreatedAt(),
						assignment.getLecturers().stream()
								.map(lecturer -> lecturer.getName() == null || lecturer.getName().isBlank() ? lecturer.getUserId() : lecturer.getName())
								.sorted(String::compareToIgnoreCase)
								.toList(),
						assignment.getLecturers().stream()
								.map(UserAccount::getUserId)
								.sorted(String::compareToIgnoreCase)
								.toList()))
				.toList();
	}

	public UserSummaryResponse getStatus(String userId) {
		UserAccount user = userRepository.findByUserId(UserAccount.normalizeUserId(userId))
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		return toSummary(user);
	}

	public String reportSuspicious(String userId, String email) {
		String normalizedUserId = UserAccount.normalizeUserId(userId);
		String normalizedEmail = UserAccount.normalize(email);
		UserAccount user = userRepository.findByUserIdAndEmail(normalizedUserId, normalizedEmail)
				.orElseThrow(() -> new IllegalArgumentException("No account found for provided user ID and email"));

		user.flagSuspicious();
		userRepository.save(user);
		return "Suspicious activity reported. Admin has been notified.";
	}

	public UserSummaryResponse updateRole(Integer userId, Role role) {
		UserAccount user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.setRole(role);
		userRepository.save(user);
		return toSummary(user);
	}

	public UserSummaryResponse deactivate(Integer userId) {
		UserAccount user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.deactivate();
		userRepository.save(user);
		return toSummary(user);
	}

	public UserSummaryResponse clearSuspicious(Integer userId) {
		UserAccount user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		user.clearSuspicious();
		userRepository.save(user);
		return toSummary(user);
	}

	public void delete(Integer userId) {
		userRepository.deleteById(userId);
	}

	private UserSummaryResponse toSummary(UserAccount user) {
		String status = user.isActive() ? "ACTIVE" : "PENDING_ACTIVATION";
		if (user.isSuspicious()) {
			status = status + "_SUSPICIOUS";
		}
		String suspiciousReason = null;
		if (user.isSuspicious()) {
			if (user.getFailedOtpAttempts() >= suspiciousThreshold) {
				suspiciousReason = "Too many failed OTP attempts";
			} else if (!user.isActive() && user.getOtpRequestCount() >= suspiciousThreshold) {
				suspiciousReason = "Too many OTP requests before activation";
			} else {
				suspiciousReason = "Reported from suspicious-account email link";
			}
		}

		return new UserSummaryResponse(
				user.getId(),
				user.getName(),
				user.getUserId(),
				user.getEmail(),
				user.getRole(),
				user.isActive(),
				user.isSuspicious(),
				user.getOtpRequestCount(),
				user.getFailedOtpAttempts(),
				suspiciousReason,
				status);
	}

	private String generateOtp() {
		return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
	}

	private String generateNextUserId(Role role) {
		String prefix = switch (role) {
			case LECTURER -> "LEC";
			case STUDENT -> "STU";
			default -> "USR";
		};

		int nextSequence = userRepository.findTopByUserIdStartingWithOrderByUserIdDesc(prefix)
				.map(UserAccount::getUserId)
				.map(existingId -> existingId.replaceAll("[^0-9]", ""))
				.filter(number -> !number.isBlank())
				.map(Integer::parseInt)
				.map(current -> current + 1)
				.orElse(1);

		return "%s%03d".formatted(prefix, nextSequence);
	}

	private String formatSchedule(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
		StringBuilder builder = new StringBuilder();
		if (startDate != null) {
			builder.append(startDate);
			if (endDate != null && !endDate.equals(startDate)) {
				builder.append(" to ").append(endDate);
			}
		}
		if (startTime != null) {
			if (!builder.isEmpty()) {
				builder.append(" at ");
			} else {
				builder.append("Time ");
			}
			builder.append(startTime);
			if (endTime != null && !endTime.equals(startTime)) {
				builder.append(" to ").append(endTime);
			}
		}
		return builder.isEmpty() ? "No schedule provided" : builder.toString();
	}
}