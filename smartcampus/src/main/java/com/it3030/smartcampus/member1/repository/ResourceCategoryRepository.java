package com.it3030.smartcampus.member1.repository;

import com.it3030.smartcampus.member1.model.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Long> {
    
    Optional<ResourceCategory> findByName(String name);
    
    boolean existsByName(String name);
    
    List<ResourceCategory> findByNameContainingIgnoreCase(String name);
    
    List<ResourceCategory> findByOrderByNameAsc();
}
