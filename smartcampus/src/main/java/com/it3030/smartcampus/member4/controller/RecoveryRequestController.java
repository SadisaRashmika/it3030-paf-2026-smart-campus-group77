package com.it3030.smartcampus.member4.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it3030.smartcampus.member4.dto.RecoveryRequestResponse;
import com.it3030.smartcampus.member4.dto.RecoveryRequestSubmissionRequest;
import com.it3030.smartcampus.member4.dto.RecoveryRequestSubmissionResponse;
import com.it3030.smartcampus.member4.service.RecoveryRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class RecoveryRequestController {

	private final RecoveryRequestService recoveryRequestService;

	public RecoveryRequestController(RecoveryRequestService recoveryRequestService) {
		this.recoveryRequestService = recoveryRequestService;
	}

	@PostMapping("/public/recovery-requests")
	public ResponseEntity<RecoveryRequestSubmissionResponse> submit(@Valid @RequestBody RecoveryRequestSubmissionRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(recoveryRequestService.submit(request));
	}

	@GetMapping("/admin/recovery-requests")
	public ResponseEntity<List<RecoveryRequestResponse>> list() {
		return ResponseEntity.ok(recoveryRequestService.listAll());
	}

	@PatchMapping("/admin/recovery-requests/{requestId}/approve")
	public ResponseEntity<RecoveryRequestResponse> approve(@PathVariable Long requestId) {
		return ResponseEntity.ok(recoveryRequestService.approve(requestId));
	}

	@PatchMapping("/admin/recovery-requests/{requestId}/reject")
	public ResponseEntity<RecoveryRequestResponse> reject(@PathVariable Long requestId) {
		return ResponseEntity.ok(recoveryRequestService.reject(requestId));
	}
}
