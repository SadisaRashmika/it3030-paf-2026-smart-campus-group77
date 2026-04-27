package com.it3030.smartcampus.member4.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.it3030.smartcampus.member4.dto.NotificationResponse;
import com.it3030.smartcampus.member4.model.Notification;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.NotificationRepository;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
		this.notificationRepository = notificationRepository;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public List<NotificationResponse> myNotifications(String principal) {
		UserAccount user = resolveUser(principal);
		return notificationRepository.findByUser_IdOrderByCreatedAtDesc(user.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public NotificationResponse createLoginAlert(String principal, String channel) {
		if (principal == null || principal.isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		UserAccount user = resolveUser(principal);
		String message = "You logged in successfully.";
		Notification saved = notificationRepository.save(Notification.of(user, message));
		return toResponse(saved);
	}

	@Transactional
	public void createPasswordChangeAlert(String principal) {
		if (principal == null || principal.isBlank()) {
			return;
		}

		resolveUserOptional(principal).ifPresent(user -> {
			String message = "Security Alert: Your account password was changed successfully.";
			notificationRepository.save(Notification.of(user, message));
		});
	}

	@Transactional
	public void createSystemNotification(UserAccount user, String message) {
		if (user == null || user.getId() == null) {
			return;
		}

		if (message == null || message.isBlank()) {
			return;
		}

		notificationRepository.save(Notification.of(user, message.trim()));
	}

	@Transactional
	public void createSystemNotification(String principalOrUserId, String message) {
		if (principalOrUserId == null || principalOrUserId.isBlank()) {
			return;
		}

		if (message == null || message.isBlank()) {
			return;
		}

		resolveUserOptional(principalOrUserId).ifPresent(user ->
				notificationRepository.save(Notification.of(user, message.trim())));
	}

	@Transactional
	public NotificationResponse markRead(Long notificationId, String principal) {
		UserAccount user = resolveUser(principal);
		Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, user.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

		notification.markRead();
		notificationRepository.save(notification);
		return toResponse(notification);
	}

	@Transactional
	public int markAllRead(String principal) {
		UserAccount user = resolveUser(principal);
		List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
		int updatedCount = 0;
		for (Notification notification : notifications) {
			if (!notification.isRead()) {
				notification.markRead();
				updatedCount++;
			}
		}

		if (updatedCount > 0) {
			notificationRepository.saveAllAndFlush(notifications);
		}

		return updatedCount;
	}

	@Transactional
	public void delete(Long notificationId, String principal) {
		UserAccount user = resolveUser(principal);
		Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, user.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
		notificationRepository.delete(notification);
	}

	private UserAccount resolveUser(String principal) {
		return resolveUserOptional(principal)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
	}

	private java.util.Optional<UserAccount> resolveUserOptional(String principal) {
		if (principal == null || principal.isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		String normalized = principal.trim();
		if (normalized.isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		if (normalized.contains("@")) {
			return userRepository.findByEmail(normalized.toLowerCase())
					.or(() -> userRepository.findByUserId(normalized.toUpperCase()));
		}

		return userRepository.findByUserId(normalized.toUpperCase())
				.or(() -> userRepository.findByEmail(normalized.toLowerCase()));
	}

	private NotificationResponse toResponse(Notification notification) {
		return new NotificationResponse(
				notification.getId(),
				notification.getUser().getUserId(),
				notification.getMessage(),
				notification.isRead(),
				notification.getCreatedAt());
	}
}
