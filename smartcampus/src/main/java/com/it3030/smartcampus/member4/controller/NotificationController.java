package com.it3030.smartcampus.member4.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it3030.smartcampus.member4.dto.NotificationResponse;
import com.it3030.smartcampus.member4.dto.ReadAllNotificationsResponse;
import com.it3030.smartcampus.member4.service.NotificationService;

@RestController
@RequestMapping("/api/member4/notifications")
@Validated
public class NotificationController {

	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping("/me")
	public ResponseEntity<List<NotificationResponse>> myNotifications(Authentication authentication) {
		return ResponseEntity.ok(notificationService.myNotifications(authentication.getName()));
	}

	@PatchMapping("/{id}/read")
	public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id, Authentication authentication) {
		return ResponseEntity.ok(notificationService.markRead(id, authentication.getName()));
	}

	@PatchMapping("/read-all")
	public ResponseEntity<ReadAllNotificationsResponse> markAllRead(Authentication authentication) {
		int updatedCount = notificationService.markAllRead(authentication.getName());
		return ResponseEntity.ok(new ReadAllNotificationsResponse(updatedCount));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
		notificationService.delete(id, authentication.getName());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
