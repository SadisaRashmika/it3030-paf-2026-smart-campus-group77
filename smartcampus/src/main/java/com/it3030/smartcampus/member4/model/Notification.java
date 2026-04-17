package com.it3030.smartcampus.member4.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount user;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	@Column(name = "is_read", nullable = false)
	private boolean read;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	protected Notification() {
	}

	public static Notification of(UserAccount user, String message) {
		Notification notification = new Notification();
		notification.user = user;
		notification.message = message;
		notification.read = false;
		return notification;
	}

	public void markRead() {
		this.read = true;
	}

	public Long getId() {
		return id;
	}

	public UserAccount getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}

	public boolean isRead() {
		return read;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}
}
