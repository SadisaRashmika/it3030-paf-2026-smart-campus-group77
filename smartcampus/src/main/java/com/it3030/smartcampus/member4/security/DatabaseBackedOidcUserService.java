package com.it3030.smartcampus.member4.security;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.it3030.smartcampus.member4.model.Role;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Service
public class DatabaseBackedOidcUserService extends OidcUserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DatabaseBackedOidcUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		OidcUser oidcUser = super.loadUser(userRequest);

		String email = oidcUser.getEmail() == null ? null : oidcUser.getEmail().trim().toLowerCase();
		if (email == null || email.isBlank()) {
			throw new OAuth2AuthenticationException("Google account email was not provided");
		}

		String displayName = oidcUser.getFullName();
		if (displayName == null || displayName.isBlank()) {
			displayName = oidcUser.getGivenName();
		}
		final String resolvedDisplayName = displayName;

		UserAccount account = userRepository.findByEmail(email)
				.map(existing -> updateExistingUser(existing, resolvedDisplayName))
				.orElseGet(() -> createOAuthStudent(email, resolvedDisplayName));

		Set<SimpleGrantedAuthority> mappedAuthorities = new LinkedHashSet<>();
		mappedAuthorities.add(new SimpleGrantedAuthority(account.getRole().authority()));

		return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
	}

	private UserAccount updateExistingUser(UserAccount account, String displayName) {
		if (!account.isActive()) {
			throw new OAuth2AuthenticationException("Account is deactivated. Please activate your account before signing in.");
		}

		boolean changed = false;

		if (displayName != null && !displayName.isBlank() && (account.getName() == null || account.getName().isBlank())) {
			account.setName(displayName);
			changed = true;
		}

		if (changed) {
			return userRepository.save(account);
		}

		return account;
	}

	private UserAccount createOAuthStudent(String email, String displayName) {
		String userId = generateNextStudentId();
		String password = passwordEncoder.encode("oauth2-account");
		UserAccount user = UserAccount.activeUser(userId, email, password, Role.STUDENT);
		if (displayName != null && !displayName.isBlank()) {
			user.setName(displayName);
		}
		return userRepository.save(user);
	}

	private String generateNextStudentId() {
		int nextSequence = userRepository.findTopByUserIdStartingWithOrderByUserIdDesc("STU")
				.map(UserAccount::getUserId)
				.map(existingId -> existingId.replaceAll("[^0-9]", ""))
				.filter(number -> !number.isBlank())
				.map(Integer::parseInt)
				.map(current -> current + 1)
				.orElse(1);

		return "STU%03d".formatted(nextSequence);
	}
}
