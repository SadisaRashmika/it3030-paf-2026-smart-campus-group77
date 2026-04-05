package com.it3030.smartcampus.member4.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PlainOrBcryptPasswordEncoder implements PasswordEncoder {

	private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

	@Override
	public String encode(CharSequence rawPassword) {
		return bcrypt.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (encodedPassword == null) {
			return false;
		}

		if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
			return bcrypt.matches(rawPassword, encodedPassword);
		}

		return encodedPassword.contentEquals(rawPassword);
	}
}