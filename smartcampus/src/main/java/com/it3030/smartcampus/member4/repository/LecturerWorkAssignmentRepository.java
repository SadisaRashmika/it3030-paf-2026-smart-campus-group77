package com.it3030.smartcampus.member4.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;

import org.springframework.data.jpa.repository.JpaRepository;

import com.it3030.smartcampus.member4.model.LecturerWorkAssignment;

public interface LecturerWorkAssignmentRepository extends JpaRepository<LecturerWorkAssignment, Long> {

	@EntityGraph(attributePaths = "lecturers")
	List<LecturerWorkAssignment> findAllByOrderByCreatedAtDesc();
}