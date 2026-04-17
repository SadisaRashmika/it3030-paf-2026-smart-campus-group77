package com.it3030.smartcampus.member2.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;

import com.it3030.smartcampus.member1.model.Resource;
import com.it3030.smartcampus.member4.model.UserAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "timetable_entries")
public class TimetableEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "resource_id", nullable = false)
	private Resource resource;

	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_week", nullable = false, length = 10)
	private DayOfWeek dayOfWeek;

	@Column(name = "start_time", nullable = false)
	private LocalTime startTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Column(nullable = false)
	private String title;

	@Column(length = 500)
	private String description;

	@ManyToOne
	@JoinColumn(name = "created_by")
	private UserAccount createdBy;

	@Column(name = "created_at", updatable = false)
	private Instant createdAt = Instant.now();

	public TimetableEntry() {}

	public Long getId() { return id; }
	public Resource getResource() { return resource; }
	public void setResource(Resource resource) { this.resource = resource; }
	public DayOfWeek getDayOfWeek() { return dayOfWeek; }
	public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
	public LocalTime getStartTime() { return startTime; }
	public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
	public LocalTime getEndTime() { return endTime; }
	public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	public UserAccount getCreatedBy() { return createdBy; }
	public void setCreatedBy(UserAccount createdBy) { this.createdBy = createdBy; }
	public Instant getCreatedAt() { return createdAt; }
}
