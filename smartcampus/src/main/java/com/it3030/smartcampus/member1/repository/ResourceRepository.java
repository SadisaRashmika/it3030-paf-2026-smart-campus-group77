package com.it3030.smartcampus.member1.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.it3030.smartcampus.member1.model.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
	List<Resource> findByType(String type);
	List<Resource> findByAvailable(boolean available);
}
