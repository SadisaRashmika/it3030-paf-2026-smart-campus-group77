package com.it3030.smartcampus.member4.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.it3030.smartcampus.member4.model.Role;
import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;
import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member1.repository.ResourceRepository;

@Configuration
public class BootstrapDataService {

	@Bean
	@SuppressWarnings("unused")
	CommandLineRunner seedData(UserRepository userRepository,
							   ResourceRepository resourceRepository,
							   PasswordEncoder passwordEncoder,
							   @Value("${app.security.admin-email}") String adminEmail,
							   @Value("${app.security.admin-password}") String adminPassword,
							   @Value("${app.security.admin-user-id}") String adminUserId) {
		return args -> {
			upsertUser(userRepository, passwordEncoder, "ADMIN001", adminEmail, adminPassword, Role.ADMIN);
			upsertUser(userRepository, passwordEncoder, "MGR101", "dip30kalindugeethanjana@gmail.com", "12345", Role.TIMETABLE_MANAGER);
			upsertUser(userRepository, passwordEncoder, "MGR102", "kyronx99@gmail.com", "12345", Role.TIMETABLE_MANAGER);
			upsertUser(userRepository, passwordEncoder, "LEC101", "kalindugeethanjana48@gmail.com", "12345", Role.LECTURER);
			upsertUser(userRepository, passwordEncoder, "STU001", "student@smartcampus.local", "12345", Role.STUDENT);


			seedResources(resourceRepository);
		};
	}

	private void seedResources(ResourceRepository resourceRepository) {
		if (resourceRepository.count() == 0) {
			resourceRepository.save(createResource("Lecture Hall A", "HALL", 100));
			resourceRepository.save(createResource("Computer Lab 01", "LAB", 30));
			resourceRepository.save(createResource("Seminar Room 4", "ROOM", 20));
			resourceRepository.save(createResource("Main Auditorium", "AUDITORIUM", 500));
		}
	}

	private Resource createResource(String name, String type, int capacity) {
		Resource res = new Resource();
		res.setName(name);
		res.setType(type);
		res.setCapacity(capacity);
		res.setAvailable(true);
		return res;
	}

	private void upsertUser(UserRepository userRepository,
							   PasswordEncoder passwordEncoder,
							   String userId,
							   String email,
							   String rawPassword,
							   Role role) {
		userRepository.findByEmail(email).ifPresentOrElse(existing -> {
			existing.setPasswordHash(passwordEncoder.encode(rawPassword));
			existing.setRole(role);
			existing.activate(existing.getPasswordHash());
			userRepository.save(existing);
		}, () -> {
			UserAccount user = UserAccount.activeUser(userId, email, passwordEncoder.encode(rawPassword), role);
			user.setName("Default User");
			userRepository.save(user);
		});
	}
}