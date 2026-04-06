package com.it3030.smartcampus.member4.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

	public List<NotificationResponse> myNotifications(String email) {
		UserAccount user = getUserByEmail(email);
		return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	public NotificationResponse markRead(Long notificationId, String email) {
		UserAccount user = getUserByEmail(email);
		Notification notification = notificationRepository.findByIdAndUserId(notificationId, user.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

		notification.markRead();
		notificationRepository.save(notification);
		return toResponse(notification);
	}

	public int markAllRead(String email) {
		UserAccount user = getUserByEmail(email);
		List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
		int updatedCount = 0;
		for (Notification notification : notifications) {
			if (!notification.isRead()) {
				notification.markRead();
				updatedCount++;
			}
		}

		if (updatedCount > 0) {
			notificationRepository.saveAll(notifications);
		}

		return updatedCount;
	}

	public void delete(Long notificationId, String email) {
		UserAccount user = getUserByEmail(email);
		Notification notification = notificationRepository.findByIdAndUserId(notificationId, user.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
		notificationRepository.delete(notification);
	}

	private UserAccount getUserByEmail(String email) {
		if (email == null || email.isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		return userRepository.findByEmail(email.trim().toLowerCase())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
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
