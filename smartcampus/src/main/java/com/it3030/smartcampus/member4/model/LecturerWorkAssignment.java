package com.it3030.smartcampus.member4.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "lecturer_work_assignments")
public class LecturerWorkAssignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "work_title", nullable = false)
	private String workTitle;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private String location;

	@Column(name = "start_date")
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "start_time")
	private LocalTime startTime;

	@Column(name = "end_time")
	private LocalTime endTime;

	@Column(name = "send_email", nullable = false)
	private boolean sendEmail;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "lecturer_work_assignment_recipients",
			joinColumns = @JoinColumn(name = "assignment_id", nullable = false),
			inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false))
	private Set<UserAccount> lecturers = new LinkedHashSet<>();

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	protected LecturerWorkAssignment() {
	}

	public static LecturerWorkAssignment of(String workTitle,
									 String description,
									 String location,
									 LocalDate startDate,
									 LocalDate endDate,
									 LocalTime startTime,
									 LocalTime endTime,
									 boolean sendEmail,
									 Set<UserAccount> lecturers) {
		LecturerWorkAssignment assignment = new LecturerWorkAssignment();
		assignment.workTitle = workTitle;
		assignment.description = description;
		assignment.location = location;
		assignment.startDate = startDate;
		assignment.endDate = endDate;
		assignment.startTime = startTime;
		assignment.endTime = endTime;
		assignment.sendEmail = sendEmail;
		assignment.lecturers = new LinkedHashSet<>(lecturers);
		return assignment;
	}

	public Long getId() {
		return id;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public Set<UserAccount> getLecturers() {
		return lecturers;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}