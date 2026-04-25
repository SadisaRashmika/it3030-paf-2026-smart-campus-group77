package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByAvailable(Boolean available);
}
