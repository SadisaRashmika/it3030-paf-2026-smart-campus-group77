package com.it3030.smartcampus.member4.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.it3030.smartcampus.member4.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);

	Optional<Notification> findByIdAndUserId(Long id, Integer userId);
}
