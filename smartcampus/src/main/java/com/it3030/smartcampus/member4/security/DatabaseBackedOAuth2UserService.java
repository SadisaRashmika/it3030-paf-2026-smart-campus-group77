package com.it3030.smartcampus.member4.security;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.it3030.smartcampus.member4.model.Role;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Service
public class DatabaseBackedOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DatabaseBackedOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = delegate.loadUser(userRequest);
		Map<String, Object> attributes = oauth2User.getAttributes();

		String email = normalizedAttribute(attributes, "email");
		if (email == null || email.isBlank()) {
			throw new OAuth2AuthenticationException("Google account email was not provided");
		}

		String displayName = normalizedAttribute(attributes, "name");
		UserAccount account = userRepository.findByEmail(email)
				.map(existing -> updateExistingUser(existing, displayName))
				.orElseGet(() -> createOAuthStudent(email, displayName));

		Set<SimpleGrantedAuthority> mappedAuthorities = new LinkedHashSet<>();
		mappedAuthorities.add(new SimpleGrantedAuthority(account.getRole().authority()));

		return new DefaultOAuth2User(mappedAuthorities, attributes, "email");
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

	private String normalizedAttribute(Map<String, Object> attributes, String key) {
		Object value = attributes.get(key);
		if (!(value instanceof String str)) {
			return null;
		}

		String trimmed = str.trim();
		return trimmed.isEmpty() ? null : ("email".equals(key) ? trimmed.toLowerCase() : trimmed);
	}
}
