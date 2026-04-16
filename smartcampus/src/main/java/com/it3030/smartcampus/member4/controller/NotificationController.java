package com.it3030.smartcampus.member4.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
		return ResponseEntity.ok(notificationService.myNotifications(resolvePrincipal(authentication)));
	}

	@PatchMapping("/{id}/read")
	public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id, Authentication authentication) {
		return ResponseEntity.ok(notificationService.markRead(id, resolvePrincipal(authentication)));
	}

	@PatchMapping("/read-all")
	public ResponseEntity<ReadAllNotificationsResponse> markAllRead(Authentication authentication) {
		int updatedCount = notificationService.markAllRead(resolvePrincipal(authentication));
		return ResponseEntity.ok(new ReadAllNotificationsResponse(updatedCount));
	}

	@PostMapping("/login-alert")
	public ResponseEntity<NotificationResponse> createLoginAlert(
			Authentication authentication,
			@RequestBody(required = false) java.util.Map<String, String> payload) {
		String channel = payload == null ? null : payload.get("channel");
		NotificationResponse response = notificationService.createLoginAlert(resolvePrincipal(authentication), channel);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
		notificationService.delete(id, resolvePrincipal(authentication));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	private String resolvePrincipal(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oauth2User) {
			Object emailAttr = oauth2User.getAttributes().get("email");
			if (emailAttr instanceof String email && !email.isBlank()) {
				return email.trim().toLowerCase();
			}
		}

		return authentication == null ? null : authentication.getName();
	}
}
