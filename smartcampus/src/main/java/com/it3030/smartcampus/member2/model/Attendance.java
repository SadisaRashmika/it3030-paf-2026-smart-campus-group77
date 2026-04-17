package com.it3030.smartcampus.member2.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.it3030.smartcampus.member4.model.UserAccount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "booking_id"})
})
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount student;

	@ManyToOne
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking booking;

	@CreationTimestamp
	@Column(name = "marked_at", updatable = false)
	private Instant markedAt;

	public Attendance() {
	}

	public Attendance(UserAccount student, Booking booking) {
		this.student = student;
		this.booking = booking;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserAccount getStudent() {
		return student;
	}

	public void setStudent(UserAccount student) {
		this.student = student;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public Instant getMarkedAt() {
		return markedAt;
	}
}
