package com.it3030.smartcampus.member4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.it3030.smartcampus.member4.dto.AuthUserResponse;
import com.it3030.smartcampus.member4.dto.ForgotPasswordRequest;
import com.it3030.smartcampus.member4.dto.ForgotPasswordResetRequest;
import com.it3030.smartcampus.member4.dto.LoginRequest;
import com.it3030.smartcampus.member4.dto.MessageResponse;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;
import com.it3030.smartcampus.member4.service.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/public/auth")
@Validated
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordResetService passwordResetService;

	public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordResetService passwordResetService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordResetService = passwordResetService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthUserResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
		String principal = resolveLoginPrincipal(request);

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(principal, request.password()));

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		HttpSession session = httpRequest.getSession(true);
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

		return ResponseEntity.ok(toAuthUserResponse(authentication));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		SecurityContextHolder.clearContext();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PostMapping("/forgot-password/send-otp")
	public ResponseEntity<MessageResponse> sendForgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequest request) {
		passwordResetService.sendOtp(request);
		return ResponseEntity.ok(new MessageResponse("OTP sent to your email"));
	}

	@PostMapping("/forgot-password/reset")
	public ResponseEntity<MessageResponse> resetForgottenPassword(@Valid @RequestBody ForgotPasswordResetRequest request) {
		passwordResetService.resetPassword(request);
		return ResponseEntity.ok(new MessageResponse("Password reset successful. You can now login."));
	}

	@GetMapping("/me")
	public ResponseEntity<AuthUserResponse> me(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.ok(toAuthUserResponse(authentication));
	}

	private AuthUserResponse toAuthUserResponse(Authentication authentication) {
		String principal = resolvePrincipal(authentication);
		UserAccount user = findAuthenticatedUser(principal)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

		String email = user.getEmail();
		String userId = user.getUserId();
		String name = user.getName();
		String role = user.getRole().authority();
		return new AuthUserResponse(name, email, userId, role, true);
	}

	private java.util.Optional<UserAccount> findAuthenticatedUser(String principal) {
		if (principal == null || principal.isBlank()) {
			return java.util.Optional.empty();
		}

		String normalized = principal.trim();
		if (normalized.contains("@")) {
			return userRepository.findByEmail(normalized.toLowerCase());
		}

		return userRepository.findByUserId(normalized.toUpperCase())
				.or(() -> userRepository.findByEmail(normalized.toLowerCase()));
	}

	private String resolvePrincipal(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oauth2User) {
			Object emailAttr = oauth2User.getAttributes().get("email");
			if (emailAttr instanceof String email && !email.isBlank()) {
				return email.trim().toLowerCase();
			}
		}

		return authentication.getName();
	}

	private String resolveLoginPrincipal(LoginRequest request) {
		String email = normalize(request.email());
		String userId = normalize(request.userId());
		String identifier = normalize(request.identifier());

		if (identifier != null && email == null && userId == null) {
			if (identifier.contains("@")) {
				email = identifier;
			} else {
				userId = identifier;
			}
		}

		if (userId != null) {
			return userRepository.findByUserId(userId.toUpperCase())
					.map(u -> u.getEmail())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user ID or password"));
		}

		if (email != null) {
			return email.toLowerCase();
		}

		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide either email or user ID");
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}

		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}