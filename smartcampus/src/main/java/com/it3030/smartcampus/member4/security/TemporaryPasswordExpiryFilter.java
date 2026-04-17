package com.it3030.smartcampus.member4.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class TemporaryPasswordExpiryFilter extends OncePerRequestFilter {

	private final UserRepository userRepository;

	public TemporaryPasswordExpiryFilter(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (shouldBlock(authentication)) {
			Optional<UserAccount> user = findAuthenticatedUser(authentication == null ? null : authentication.getName());
			if (user.isPresent()) {
				UserAccount account = user.get();
				if (!account.isActive()) {
					reject(request, response, "Account is deactivated. Please activate your account.");
					return;
				}

				if (account.temporaryPasswordExpired(Instant.now())) {
					reject(request, response, "Temporary password expired. Please request a new recovery request.");
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private boolean shouldBlock(Authentication authentication) {
		return authentication != null
				&& authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken)
				&& authentication.getPrincipal() instanceof UserDetails;
	}

	private Optional<UserAccount> findAuthenticatedUser(String principal) {
		if (principal == null || principal.isBlank()) {
			return Optional.empty();
		}

		String normalized = principal.trim();
		if (normalized.contains("@")) {
			return userRepository.findByEmail(normalized.toLowerCase());
		}

		return userRepository.findByUserId(normalized.toUpperCase())
				.or(() -> userRepository.findByEmail(normalized.toLowerCase()));
	}

	private void reject(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
		SecurityContextHolder.clearContext();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType("application/json");
		response.getWriter().write("{\"message\":\"" + escapeJson(message) + "\"}");
	}

	private String escapeJson(String message) {
		return message.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
