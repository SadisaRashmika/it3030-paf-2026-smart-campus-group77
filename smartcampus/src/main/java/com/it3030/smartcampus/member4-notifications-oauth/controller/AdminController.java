package com.it3030.smartcampus.member4.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it3030.smartcampus.member4.dto.UpdateRoleRequest;
import com.it3030.smartcampus.member4.dto.UserSummaryResponse;
import com.it3030.smartcampus.member4.service.ActivationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

	private final ActivationService activationService;

	public AdminController(ActivationService activationService) {
		this.activationService = activationService;
	}

	@GetMapping("/users")
	public ResponseEntity<List<UserSummaryResponse>> users() {
		return ResponseEntity.ok(activationService.listUsers());
	}

	@GetMapping("/users/suspicious")
	public ResponseEntity<List<UserSummaryResponse>> suspiciousUsers() {
		return ResponseEntity.ok(activationService.listSuspiciousUsers());
	}

	@PatchMapping("/users/{userId}/role")
	public ResponseEntity<UserSummaryResponse> assignRole(@PathVariable Long userId, @Valid @RequestBody UpdateRoleRequest request) {
		return ResponseEntity.ok(activationService.updateRole(userId, request.role()));
	}

	@PatchMapping("/users/{userId}/deactivate")
	public ResponseEntity<UserSummaryResponse> deactivate(@PathVariable Long userId) {
		return ResponseEntity.ok(activationService.deactivate(userId));
	}

	@DeleteMapping("/users/{userId}")
	public ResponseEntity<Void> delete(@PathVariable Long userId) {
		activationService.delete(userId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}