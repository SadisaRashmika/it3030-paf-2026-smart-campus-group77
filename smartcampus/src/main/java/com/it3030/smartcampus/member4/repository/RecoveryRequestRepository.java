package com.it3030.smartcampus.member4.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.it3030.smartcampus.member4.model.RecoveryRequest;

public interface RecoveryRequestRepository extends JpaRepository<RecoveryRequest, Long> {

	List<RecoveryRequest> findAllByOrderByCreatedAtDesc();
}
