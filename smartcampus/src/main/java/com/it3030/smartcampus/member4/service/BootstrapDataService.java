package com.it3030.smartcampus.member4.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

@Configuration
public class BootstrapDataService {

	@Bean
	CommandLineRunner seedUsers(UserRepository userRepository,
							   PasswordEncoder passwordEncoder,
							   @Value("${app.security.admin-email}") String adminEmail,
							   @Value("${app.security.admin-password}") String adminPassword,
							   @Value("${app.security.admin-user-id:ADMIN001}") String adminUserId) {
		return args -> {
			upsertAdminUser(userRepository, passwordEncoder, adminUserId, adminEmail, adminPassword);
		};
	}

	private void upsertAdminUser(UserRepository userRepository,
							   PasswordEncoder passwordEncoder,
							   String adminUserId,
							   String email,
							   String rawPassword) {
		userRepository.findByEmail(email).ifPresentOrElse(existing -> {
			existing.setName("Koffy Doggy");
			existing.setPasswordHash(passwordEncoder.encode(rawPassword));
			existing.setRole(com.it3030.smartcampus.member4.model.Role.ADMIN);
			existing.activate(existing.getPasswordHash());
			userRepository.save(existing);
		}, () -> {
			UserAccount admin = UserAccount.adminSeed(adminUserId, email, passwordEncoder.encode(rawPassword));
			admin.setName("Koffy Doggy");
			userRepository.save(admin);
		});
	}
}