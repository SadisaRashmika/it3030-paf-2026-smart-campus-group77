package com.it3030.smartcampus.member4.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {

	ADMIN,
	LECTURER,
	STUDENT,
	TIMETABLE_MANAGER,
	RESOURCE_ADMINISTATOR;

	@JsonCreator
	public static Role fromValue(String value) {
		if (value == null) {
			return null;
		}

		String normalized = value.trim().replace('-', '_').replace(' ', '_').toUpperCase();
		return switch (normalized) {
			case "ADMIN" -> ADMIN;
			case "LECTURER" -> LECTURER;
			case "STUDENT" -> STUDENT;
			case "TIMETABLE_MANAGER", "TIMETABLEMANAGER", "STAFF" -> TIMETABLE_MANAGER;
			case "RESOURCE_ADMINISTATOR", "RESOURCEADMINISTATOR", "RESOURCE_ADMINISTRATOR", "RESOURCEADMINISTRATOR" -> RESOURCE_ADMINISTATOR;
			default -> throw new IllegalArgumentException("Unknown role: " + value);
		};
	}

	public String authority() {
		return "ROLE_" + name();
	}
}