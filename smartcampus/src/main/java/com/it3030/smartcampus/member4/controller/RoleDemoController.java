package com.it3030.smartcampus.member4.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoleDemoController {

	@GetMapping("/student/dashboard")
	public ResponseEntity<Map<String, String>> studentDashboard() {
		return ResponseEntity.ok(Map.of("message", "Student access granted"));
	}

	@GetMapping("/lecturer/dashboard")
	public ResponseEntity<Map<String, String>> lecturerDashboard() {
		return ResponseEntity.ok(Map.of("message", "Lecturer access granted"));
	}

	@GetMapping("/admin/dashboard")
	public ResponseEntity<Map<String, String>> adminDashboard() {
		return ResponseEntity.ok(Map.of("message", "Admin access granted"));
	}
}