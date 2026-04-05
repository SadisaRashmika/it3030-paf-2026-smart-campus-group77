package com.it3030.smartcampus.member4.model;

public enum Role {

	ADMIN,
	LECTURER,
	STUDENT;

	public String authority() {
		return "ROLE_" + name();
	}
}