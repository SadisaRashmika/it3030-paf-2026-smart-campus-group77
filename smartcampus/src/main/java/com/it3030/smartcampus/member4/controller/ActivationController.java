package com.it3030.smartcampus.member4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.it3030.smartcampus.member4.dto.ActivationRequest;
import com.it3030.smartcampus.member4.dto.ActivationResponse;
import com.it3030.smartcampus.member4.dto.MessageResponse;
import com.it3030.smartcampus.member4.dto.OtpVerificationRequest;
import com.it3030.smartcampus.member4.dto.UserSummaryResponse;
import com.it3030.smartcampus.member4.service.ActivationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public/activation")
@Validated
public class ActivationController {

	private final ActivationService activationService;

	public ActivationController(ActivationService activationService) {
		this.activationService = activationService;
	}

	@PostMapping("/send-otp")
	public ResponseEntity<ActivationResponse> sendOtp(@Valid @RequestBody ActivationRequest request) {
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(activationService.requestOtp(request));
	}

	@PostMapping("/verify")
	public ResponseEntity<ActivationResponse> verify(@Valid @RequestBody OtpVerificationRequest request) {
		return ResponseEntity.ok(activationService.verifyOtpAndCreatePassword(request));
	}

	@GetMapping("/status")
	public ResponseEntity<UserSummaryResponse> status(@RequestParam String userId) {
		return ResponseEntity.ok(activationService.getStatus(userId));
	}

	@GetMapping("/report-suspicious")
	public ResponseEntity<MessageResponse> reportSuspicious(@RequestParam String userId, @RequestParam String email) {
		String message = activationService.reportSuspicious(userId, email);
		return ResponseEntity.ok(new MessageResponse(message));
	}
}